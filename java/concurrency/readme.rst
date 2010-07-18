============================
Java Concurrency
============================

.. footer:: Copyright (c) 2010 Todd D. Greenwood-Geer 

---------------------------------------------------------
What's the difference between volatile and synchronized?
---------------------------------------------------------

As a developer, you may have a *slightly* fuzzy idea as to the differences between 'volatile' and 'synchronized' variables. At a high level, both the 'volatile' and 'synchronized' keywords provide synchronization semantics for fields within a class or class instance. Individual fields may be declared volatile, and as such are guaranteed to be 'thread safe'. Applying the 'synchronized' keyword to a code block or method guarantees that the block or method must acquire a lock prior to executing the code within the block. I'm intentionaly leaving these descriptions vague, as we'll be investigate what this means in this article.

Example: Volatile::

    public class FooVolatile(){
        private volatile int i;
        public int getI(){
            return i;
        }
    }

Example: Synchronized::

    public class FooSynchronized(){
        private volatile int i;
        public synchronized int getI(){
            return i;
        }
    }

**This article addresses the question: what is the difference, if any, between these two code fragments?**

----

.. contents:: Table of Contents

----


I'll break this down into three different areas:

#. Language Definition : What does the langugage spec say? See [JLS3]_, [JPL4]_.
#. Emitted ByteCode : What does an examination of the byte code show? See [PJVM]_.
#. JVM : What are the rules the JVM is playing by? See [JVMS2]_.

By the way, if you haven't yet, check out [JAMEX]_. Neil Coffey's site is a great resource.


Byte Code
===================

If you're like me, then you like looking 'under the hood' to see what's going on. I'm reading Joshua Engel's book, *Programming for the Java Virtual Machine* [PJVM]_, and I really like the ability to analyze class files at a higher abstraction than the raw bytes. For this, Engel presents Oolong, a language uses "words and numbers in place of binary values". This means that we can convert java class files into a human readable format...but they are still class files, not java code. In this section, we're going incrementally build a java class and examine the Oolong output. This way, we can more easily understand the impact, at the byte code level, of marking a field as volatile, or a method or code block as synchronized. 

Note: Sources are available at [TODDG]_.

Class Byte Code
---------------

Let's start with the most basic class file possible:

Example: Class1.java ::

    public class Class1 {
    }

If we compile Class1.java to Class1.class, and then decompile using Gnoloo, then we wind up with the following Oolong code. Oolong is simply a human readable version of the class file, and is fully described here [PJVM]_.

Here's how I compiled and decompiled the classes::

    javac [class].java -d build.out/
    java -cp $PATH Gnoloo build.out/[class].class >  build.out/[class].j

This assumes that you've unziped the lib/0201309726_CD.zip and placed the contents in your PATH. 

Example: Class1.j  (Note the suffix 'j' for Oolong files)::

    .source Class1.java
    .class public super Class1
    .super java/lang/Object

    .method public <init> ()V
    .limit stack 1
    .limit locals 1
    .var 0 is this LClass1; from l0 to l5
    .line 1
    l0:    aload_0
    l1:    invokespecial java/lang/Object/<init> ()V
    l4:    return

    .end method


See [PVJM]_ for full details on the Oolong language. The part that I want to highlight is the following...

The .var statement is literally stating that variable 0 is the *this* class, Class1::

     .var 0 is this LClass1; from l0 to l5

A .line statement is added to assist a debugger, should one be attached. (That's also what the .source line above was for, too::

    .line 1

Push the reference to *this* stored in varible 0 onto the stack::

    l0:    aload_0

Invoke the super class init method::

    l1:    invokespecial java/lang/Object/<init> ()V

Return::

    l4:    return


TODO: verify what is being returned at the top of the stack.

This is so cool. If you don't have it, get a copy of Engel's book. 


Field Byte Code
---------------------

Ok, to continue, let's see what happens when we add a field to the class.

Example: Class2.java ::

    public class Class2 {
        private int i;
    }

Example: Class2.j ::

    .source Class2.java
    .class public super Class2
    .super java/lang/Object

    .field private i I

    .method public <init> ()V
    .limit stack 1
    .limit locals 1
    .var 0 is this LClass2; from l0 to l5
    .line 1
    l0:    aload_0
    l1:    invokespecial java/lang/Object/<init> ()V
    l4:    return

    .end method

Oolong shows that we added a new private field::

    .field private i I
    
Note that 'I' means int. If it had been an Integer, then this line would have been ".field private i Ljava.lang.Integer;" So that was not terribly exciting. We add a field, and we can see it in Oolong. No big deal.


Accessor Byte Code
------------------

Now let's add the getters and setters for our private variable.

Example: Class3.java ::

    public class Class3 {
        private int i;

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }
    }

Adding these two methods produces considerably more Oolong code. I've broken the returned class into several parts below.

Example: Class3.j : The class is the same::

    .source Class3.java
    .class public super Class3
    .super java/lang/Object

    .field private i I

    .method public <init> ()V
    .limit stack 1
    .limit locals 1
    .var 0 is this LClass3; from l0 to l5
    .line 1
    l0:    aload_0
    l1:    invokespecial java/lang/Object/<init> ()V
    l4:    return

    .end method

The basic class is the same, including the class header, the field, and the constructor.

Example: Class3.j : Getter byte code::

    .method public getI ()I
    .limit stack 1
    .limit locals 1
    .var 0 is this LClass3; from l0 to l5
    .line 5
    l0:    aload_0
    l1:    getfield Class3/i I
    l4:    ireturn

    .end method

I'll explain the getter in detail. First, we define the method::

    .method public getI ()I

This is a public method that returns an int (remember, 'I' means 'int', not Integer).

Here we're again storing *this* in variable 0::

    .var 0 is this LClass3; from l0 to l5

And again, we're pushing the reference in variable 0 (*this*) onto the stack::

    l0:    aload_0

At this point, we're invoking the getfield on the class. Notice how the field is qualified by [classname]/[fieldname]. The type is declared as in int.

    l1:    getfield Class3/i I


The last operation in the method is to return an int::

    l4:    ireturn


Example: Class3.j : And we've added a setter::

    .method public setI (I)V
    .limit stack 2
    .limit locals 2
    .var 0 is this LClass3; from l0 to l6
    .var 1 is i I from l0 to l6
    .line 9
    l0:    aload_0
    l1:    iload_1
    l2:    putfield Class3/i I
    .line 10
    l5:    return

    .end method

Let's take the setter apart. The method definition states that it has one int parameter, *I*, and it returns void, *V*::

    .method public setI (I)V

Again we declare variable 0 is a reference to *this*::

    .var 0 is this LClass3; from l0 to l6

Next we declare variable 1 is in integer. Basically, for a class instance, variable 0 is the class, and subsequent variables are the parameters passed to the method::

    .var 1 is i I from l0 to l6

Push the variables onto the stack so that they can be consumed by the putfield operaiton::

    l0:    aload_0
    l1:    iload_1

The putfield operation pops the parameter and class instance reference off the stack and sets the value of the instance field::

    l2:    putfield Class3/i I

Nothing to return, so we just return::

    l5:    return
 
Voltile Field Byte Code
-----------------------

In Class4, the only difference introduced is making the integer field 'i' volatile:

Example: Class4.java : 'i' is volatile::

    public class Class4 {
        private volatile int i;

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }
    }


Example: Class4.j : the field reference for 'i' is now marked 'volatile'::

    .source Class4.java
    .class public super Class4
    .super java/lang/Object

    .field private volatile i I

Interestingly enough, the only change to the byte code is the addition of the 'volatile' attribute to the field.


Synchronized Method Byte Code
-----------------------------

Example Class5.java : synchronize the accessors ::

    public class Class5 {
        private int i;

        public synchronized int getI() {
            return i;
        }

        public synchronized void setI(int i) {
            this.i = i;
        }
    }



Example Class5.j : the only byte code changes are in the method attributes::

    .source Class5.java
    .class public super Class5
    .super java/lang/Object

    .field private i I

    .method public <init> ()V
    .limit stack 1
    .limit locals 1
    .var 0 is this LClass5; from l0 to l5
    .line 1
    l0:    aload_0
    l1:    invokespecial java/lang/Object/<init> ()V
    l4:    return

    .end method

    .method public synchronized getI ()I
    .limit stack 1
    .limit locals 1
    .var 0 is this LClass5; from l0 to l5
    .line 5
    l0:    aload_0
    l1:    getfield Class5/i I
    l4:    ireturn

    .end method

    .method public synchronized setI (I)V
    .limit stack 2
    .limit locals 2
    .var 0 is this LClass5; from l0 to l6
    .var 1 is i I from l0 to l6
    .line 9
    l0:    aload_0
    l1:    iload_1
    l2:    putfield Class5/i I
    .line 10
    l5:    return

    .end method


Both the set and get methods are now marked as synchronized. No other changes have been made.

Synchronized Code Block Byte Code
----------------------------------


Example Class6.java : synchronize code blocks in the accessors ::

    public class Class6 {
        private int i;

        public int getI() {
            synchronized (this) {
                return i;
            }
        }

        public void setI(int i) {
            synchronized (this) {
                this.i = i;
            }
        }
    }


Example Class6.j : note the introduction of 'monitorenter' and 'monitorexit' instructions::

    .source Class6.java
    .class public super Class6
    .super java/lang/Object

    .field private i I

    .method public <init> ()V
    .limit stack 1
    .limit locals 1
    .var 0 is this LClass6; from l0 to l5
    .line 1
    l0:    aload_0
    l1:    invokespecial java/lang/Object/<init> ()V
    l4:    return

    .end method

    .method public getI ()I
    .limit stack 2
    .limit locals 3
    .catch all from l4 to l10 using l11
    .catch all from l11 to l14 using l11
    .var 0 is this LClass6; from l0 to l16
    .line 5
    l0:    aload_0
    l1:    dup
    l2:    astore_1
    l3:    monitorenter
    .line 6
    l4:    aload_0
    l5:    getfield Class6/i I
    l8:    aload_1
    l9:    monitorexit
    l10:    ireturn
    .line 7
    l11:    astore_2
    l12:    aload_1
    l13:    monitorexit
    l14:    aload_2
    l15:    athrow

    .end method

    .method public setI (I)V
    .limit stack 2
    .limit locals 4
    .catch all from l4 to l11 using l14
    .catch all from l14 to l17 using l14
    .var 0 is this LClass6; from l0 to l20
    .var 1 is i I from l0 to l20
    .line 11
    l0:    aload_0
    l1:    dup
    l2:    astore_2
    l3:    monitorenter
    .line 12
    l4:    aload_0
    l5:    iload_1
    l6:    putfield Class6/i I
    .line 13
    l9:    aload_2
    l10:    monitorexit
    l11:    goto l19
    l14:    astore_3
    l15:    aload_2
    l16:    monitorexit
    l17:    aload_3
    l18:    athrow
    .line 14
    l19:    return

    .end method

This is interesting because it shows the explicit acquire and release of the monitor on the class instance. It also shows the exception handling and the unwinding of the locks in the case of an exception. TODO: explain this better.



Language Definition
===================

* Java Memory Model
* Happens-Before Relationships
* Threads and Locks
* Actions





References
==========

.. [JLS3] Gosling, James, Joy, Bill, Steel, Guy and Bracha, Gilad. 
    *The Java Language Specification, Third Edition*. 
    Addison Wesley, 2005, ISBN 0-321-24678-0. 
    See also: http://java.sun.com/docs/books/jls/third_edition/html/j3TOC.html.

.. [JVMS2] Lindholm, Tim and Yellin, Frank. 
    *The Java Virtual Machine Specification, Second Edition*. 
    Addison Wesley, 2003, ISBN 0201432943. 
    See also http://java.sun.com/docs/books/vmspec/2nd-edition/html/VMSpecTOC.doc.html.

.. [PJVM] Engel, Joshua. 
    *Programming For The Java Virtual Machine*. 
    Addison Wesley, 1999. ISBN 0-201-30972-6.

.. [JPL4] Arnold, Ken, Gosling, James and Holmes, David. 
    *The Java Programming Language, Fourth Edition*. 
    Addison Wesley, 2009. ISBN 0-321-34980-6. 

.. [JAMEX] www.jamex.com. Neil Coffey.
    http://www.javamex.com/tutorials/double_checked_locking.shtml
    http://www.javamex.com/tutorials/synchronization_volatile.shtml
    http://www.javamex.com/tutorials/synchronization_concurrency_synchronized2.shtml    
    http://www.javamex.com/tutorials/synchronization_synchronized_method.shtml
    http://www.javamex.com/tutorials/synchronization_concurrency_7_atomic_updaters.shtml
    http://www.javamex.com/tutorials/collections/ConcurrentSkipListMap.shtml
    http://www.javamex.com/tutorials/synchronization_volatile_typical_use.shtml
    http://www.javamex.com/tutorials/double_checked_locking.shtml
    http://www.javamex.com/tutorials/double_checked_locking_fixing.shtml
    http://www.javamex.com/tutorials/synchronization_piggyback.shtml

.. [TODDG] http://github.com/ToddG/experimental/java/concurrency

