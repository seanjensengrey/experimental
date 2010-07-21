============================
Java Concurrency
============================

.. footer:: Copyright (c) 2010 Todd D. Greenwood-Geer 

:Author: Todd D. Greenwood-Geer
:Date: Mon Jul 19  2010
:Version: 0.1

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

#. Language Definition : What does the langugage spec say? See [GOSLING]_, [ARNOLD]_.
#. Emitted ByteCode : What does an examination of the byte code show? See [ENGEL]_.
#. JVM : What are the rules the JVM is playing by? See [LINDHOLM]_.

By the way, if you haven't yet, check out [COFFEY]_. Neil Coffey's site is a great resource.


Byte Code
===================

If you're like me, then you like looking 'under the hood' to see what's going on. I'm reading Joshua Engel's book, *Programming for the Java Virtual Machine* [ENGEL]_, and I really like the ability to analyze class files at a higher abstraction than the raw bytes. For this, Engel presents Oolong, a language that uses "words and numbers in place of binary values". This means that we can convert java class files into a human readable format...but they are still class files, not java code. In this section, we're going to incrementally build a java class and examine the Oolong output. This way, we can more easily understand the impact, at the byte code level, of marking a field as volatile, or a method or code block as synchronized. 

Note: Sources are available at [GREENWOOD]_.

Class Byte Code
---------------

Let's start with the most basic class file possible:

Example: Class1.java ::

    public class Class1 {
    }

If we compile Class1.java to Class1.class, and then decompile using Gnoloo, then we wind up with the following Oolong code. Again, Oolong is simply a human readable version of the class file, and is fully described here [ENGEL]_. The directives are also fully described in [LINDHOLM]_.

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
    .line 1
    l0:    aload_0
    l1:    invokespecial java/lang/Object/<init> ()V
    l4:    return

    .end method


See [ENGEL]_ for full details on the Oolong language. The part that I want to highlight is the following...

A .line statement is added to assist a debugger, should one be attached. (That's also what the .source line above was for, too::

    .line 1

Push the reference to *this* stored in varible 0 onto the stack::

    l0:    aload_0

Invoke the super class init method::

    l1:    invokespecial java/lang/Object/<init> ()V

Return nothing::

    l4:    return


This is so cool. If you don't have it, get a copy of Engel's book. 


Field Byte Code
---------------------

Ok, to continue, let's see what happens when we add a field to the class.

Example: Class2.java ::

    public class Class2 {
        private int myInt;
    }

Example: Class2.j ::
    
    .source Class2.java
    .class public super Class2
    .super java/lang/Object

    .field private myInt I

    .method public <init> ()V
    .limit stack 1
    .limit locals 1
    .line 1
    l0:    aload_0
    l1:    invokespecial java/lang/Object/<init> ()V
    l4:    return

    .end method


Oolong shows that we added a new private field::

    .field private myInt I
    
Note that 'I' means int. If it had been an Integer, then this line would have been ".field private myInt Ljava.lang.Integer;" So that was not terribly exciting. We add a field, and we can see it in Oolong. No big deal.


Accessor Byte Code
------------------

Now let's add the getters and setters for our private variable.

Example: Class3.java ::

    public class Class3 {
        private int myInt;

        public int getMyInt() {
            return myInt;
        }

        public void setMyInt(int i) {
            this.myInt = i;
        }
    }

Adding these two methods produces considerably more byte code::

Example: Class3.j ::

    .source Class3.java
    .class public super Class3
    .super java/lang/Object

    .field private myInt I

    .method public <init> ()V
    .limit stack 1
    .limit locals 1
    .line 1
    l0:    aload_0
    l1:    invokespecial java/lang/Object/<init> ()V
    l4:    return

    .end method

    .method public getMyInt ()I
    .limit stack 1
    .limit locals 1
    .line 5
    l0:    aload_0
    l1:    getfield Class3/myInt I
    l4:    ireturn

    .end method

    .method public setMyInt (I)V
    .limit stack 2
    .limit locals 2
    .line 9
    l0:    aload_0
    l1:    iload_1
    l2:    putfield Class3/myInt I
    .line 10
    l5:    return

    .end method

The basic class is the same, including the class header, the field, and the constructor.

Example: Class3.j : Getter byte code::

    .method public getMyInt ()I
    .limit stack 1
    .limit locals 1
    .line 5
    l0:    aload_0
    l1:    getfield Class3/myInt I
    l4:    ireturn

    .end method

I'll explain the getter in detail. First, we define the method::

    .method public getMyInt ()I

This is a public method that returns an int (remember, 'I' means 'int', not Integer).

Variable 0 of a class instance refers to the *this* reference. Push the reference in variable 0 (*this*) onto the stack::

    l0:    aload_0

At this point, we're invoking the getfield on the class instance. Notice how the field is qualified by [classname]/[fieldname]. The type is declared as in int.

    l1:    getfield Class3/myInt I

The JVM Spec [JVMS]_(page 248) defines the getfield operator format for getfield as [getfield][indexbyte1][indexbyte2]. So l1 is really: [getfield][*Class3/myInt*][*I*]. This operator takes the objectref off the stack and returns a value.


The last operation replaced the *this* reference with an int value, which we now return::

    l4:    ireturn


Example: Class3.j : And we've added a setter::

    .method public setMyInt (I)V
    .limit stack 2
    .limit locals 2
    .line 9
    l0:    aload_0
    l1:    iload_1
    l2:    putfield Class3/myInt I
    .line 10
    l5:    return

    .end method

Let's take the setter apart. The method definition states that it has one int parameter, *I*, and it returns void, *V*::

    .method public setMyInt (I)V

Next we declare variable 1 is in integer. Basically, for a class instance, variable 0 is the class instance, and subsequent variables are the parameters passed to the method. Push the variables onto the stack so that they can be consumed by the putfield operation::

    l0:    aload_0
    l1:    iload_1

The putfield operator is very similar to the getfield operator [JVMS]_(page 348)::

    l2:    putfield Class3/myInt I

The putfield operator format is [putfield][indexbyte1][indexbyte2]. This translates to [putfield][*Class3/myInt*][*I*]. The operator pops the objectref and value off the stack. Nothing to return, so we just return::

    l5:    return
 
Voltile Field Byte Code
-----------------------

In Class4, the only difference introduced is making the integer field 'myInt' volatile:

Example: Class4.java : 'myInt' is volatile::


    public class Class4 {
        private volatile int myVolatileInt;

        public int getMyVolatileInt() {
            return myVolatileInt;
        }

        public void setMyVolatileInt(int i) {
            this.myVolatileInt = i;
        }
    }

Example: Class4.j : the field reference for 'i' is now marked 'volatile'::

    .source Class4.java
    .class public super Class4
    .super java/lang/Object

    .field private volatile myVolatileInt I

    .method public <init> ()V
    .limit stack 1
    .limit locals 1
    .line 1
    l0:    aload_0
    l1:    invokespecial java/lang/Object/<init> ()V
    l4:    return

    .end method

    .method public getMyVolatileInt ()I
    .limit stack 1
    .limit locals 1
    .line 5
    l0:    aload_0
    l1:    getfield Class4/myVolatileInt I
    l4:    ireturn

    .end method

    .method public setMyVolatileInt (I)V
    .limit stack 2
    .limit locals 2
    .line 9
    l0:    aload_0
    l1:    iload_1
    l2:    putfield Class4/myVolatileInt I
    .line 10
    l5:    return

    .end method


Interestingly enough, the only change to the byte code is the addition of the 'volatile' attribute to the field. (Well, I renamed the variable to make it clear that this is an integer that's declared as volatile...but that's just a nameing change).


Synchronized Method Byte Code
-----------------------------

Example Class5.java : synchronize the accessors ::

    public class Class5 {
        private int myInt;

        public synchronized int getMyInt() {
            return myInt;
        }

        public synchronized void setMyInt(int i) {
            this.myInt = i;
        }
    }


Example Class5.j : the only byte code changes are in the method attributes::

    .source Class5.java
    .class public super Class5
    .super java/lang/Object

    .field private myInt I

    .method public <init> ()V
    .limit stack 1
    .limit locals 1
    .line 1
    l0:    aload_0
    l1:    invokespecial java/lang/Object/<init> ()V
    l4:    return

    .end method

    .method public synchronized getMyInt ()I
    .limit stack 1
    .limit locals 1
    .line 5
    l0:    aload_0
    l1:    getfield Class5/myInt I
    l4:    ireturn

    .end method

    .method public synchronized setMyInt (I)V
    .limit stack 2
    .limit locals 2
    .line 9
    l0:    aload_0
    l1:    iload_1
    l2:    putfield Class5/myInt I
    .line 10
    l5:    return

    .end method


Both the set and get methods are now marked as synchronized. No other changes have been made.

Synchronized Code Block Byte Code
----------------------------------


Example Class6.java : synchronize code blocks in the accessors ::

    public class Class6 {
        private int myInt;

        public int getMyInt() {
            synchronized (this) {
                return myInt;
            }
        }

        public void setMyInt(int i) {
            synchronized (this) {
                this.myInt = i;
            }
        }
    }

This minor looking change has introduced a host of changes in the generated byte code. First of all, there are 'monitorenter' and 'monitorexit' istructions. This is an explicit, bytecode level use of the monitor on the class instance, where it was implicit in Example 5 where we synchronized the method. 

Complete Class
++++++++++++++

Example Class6.j (complete)::

    .source Class6.java
    .class public super Class6
    .super java/lang/Object

    .field private myInt I

    .method public <init> ()V
    .limit stack 1
    .limit locals 1
    .line 1
    l0:    aload_0
    l1:    invokespecial java/lang/Object/<init> ()V
    l4:    return

    .end method

    .method public getMyInt ()I
    .limit stack 2
    .limit locals 3
    .catch all from l4 to l10 using l11
    .catch all from l11 to l14 using l11
    .line 5
    l0:    aload_0
    l1:    dup
    l2:    astore_1
    l3:    monitorenter
    .line 6
    l4:    aload_0
    l5:    getfield Class6/myInt I
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

    .method public setMyInt (I)V
    .limit stack 2
    .limit locals 4
    .catch all from l4 to l11 using l14
    .catch all from l14 to l17 using l14
    .line 11
    l0:    aload_0
    l1:    dup
    l2:    astore_2
    l3:    monitorenter
    .line 12
    l4:    aload_0
    l5:    iload_1
    l6:    putfield Class6/myInt I
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


Class Header
++++++++++++

Let's break this down, line by line...

Declare the source file, usefull for debugging::

    .source Class6.java

Declare the class as 'Class6'::

    .class public super Class6

Declare the super class as Object::

    .super java/lang/Object

Declare the private int field::

    .field private myInt I


Constructor
+++++++++++

Declare the public constructor::

    .method public <init> ()V

Stack stuff that the compiler would infer if it wasn't provided::

    .limit stack 1
    .limit locals 1

Debugger info::

    .line 1

Load the variable 0, the *this* reference, onto the operand stack::

    l0:    aload_0

Operand stack == [*this*]

Invokespecial directly invokes the super class's init() method, bypassing the normal virtual dispatch mechanism::

    l1:    invokespecial java/lang/Object/<init> ()V

Return void from this method:: 

    l4:    return

End of method::

    .end method


Getter
++++++

Getter Method::

    .method public getMyInt ()I
    .limit stack 2
    .limit locals 3
    .catch all from l4 to l10 using l11
    .catch all from l11 to l14 using l11
    .line 5
    l0:    aload_0
    l1:    dup
    l2:    astore_1
    l3:    monitorenter
    .line 6
    l4:    aload_0
    l5:    getfield Class6/myInt I
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

Now things are going to get interesting. Both the get and set methods now have explicit 'monitorenter' and 'monitorexit' operands, as well as catch blocks, and a throws clause::

    .method public getMyInt ()I

Stack stuff...::

    .limit stack 2
    .limit locals 3

Two catch blocks are defined, one for the method, and one for the exception handler::

    .catch all from l4 to l10 using l11

This is the catch block for the handler, note how we're catching from l11 to l14, and assigning to the handler at l11::

    .catch all from l11 to l14 using l11


Debugger stuff::

    .line 5

Push the reference to *this* in variable 0 onto the operand stack::

    l0:    aload_0

The operand stack is now: [*this*]

Duplicate the reference copying the top item on the operand stack and pushing it on the stack::

    l1:    dup

The operand stack is now: [*this*, *this*].

Pop one of the references to *this* off the operand stack and store in a local variable, 1::

    l2:    astore_1

The operand stack is now: [*this*].

Enter the critical section by popping the reference off the stack and taking/incrementing a lock on that reference::

    l3:    monitorenter

The operand stack is now: [].

Debugger::

    .line 6

Push the reference to *this* in variable 0 onto the operand stack::

    l4:    aload_0

The operand stack is now: [*this*].

Invoke getField an instance of Class6/i and return an integer::

    l5:    getfield Class6/myInt I

The operand stack is now: [*this*, (integer value)]

Get the reference object that we used for monitorenter, and push onto the stack::

    l8:    aload_1

The operand stack is now: [*this*, (integer value), *this*]. Monitor exit pops that referenece off the stack and releases/decrements it's lock on that object::

    l9:    monitorexit

The operand stack is now: [*this*, (integer value)].

Return the integer value on the top of the stack::

    l10:    ireturn

Debugger::

    .line 7

L11 was declared as an exception handler in the catch directive above. This is not totally clear to me, but what's happening is the reference on the top of the stack is stored in variable 2. Then variable 1, the *this* reference, is loaded onto the stack, and the monitorexit decrements/releases the lock on that object::

    l11:    astore_2

Load the *this* reference tucked away in varable 1 so that the monitorexit can decrement/release the lock on it::

    l12:    aload_1
    l13:    monitorexit

Reload whatever reference was on the top of the stack from variable 2, and then throw out of this method using that reference::

    l14:    aload_2
    l15:    athrow

End method::

    .end method


Setter
+++++++

The setter is much the same as the getter.

Byte Code Summary
++++++++++++++++++

So, in summary, we were able to examine the byte code for a simple set of classes that used either 'volatile' or 'synchronized' keywords to insure thread safety of a single mutable field. I was hoping that this would clearly show that these are either functionally the same or different from the perspective of the JVM. However, while we can infer some of the JVM behaviors from the byte code in Class6, this is not definitive. So, we're going to have to peer under the hood and look closely at the definition of the Language and the JVM in order to clarify this question further.

Language Definition
===================

TODO:

* Java Memory Model
* Happens-Before Relationships
* Threads and Locks
* Actions

JVM Definition
===================

TODO:


References
==========

.. [GOSLING] Gosling, James, Joy, Bill, Steel, Guy and Bracha, Gilad. 
    *The Java Language Specification, Third Edition*. 
    Addison Wesley, 2005, ISBN 0-321-24678-0. 
    See also: http://java.sun.com/docs/books/jls/third_edition/html/j3TOC.html.

.. [LINDHOLM] Lindholm, Tim and Yellin, Frank. 
    *The Java Virtual Machine Specification, Second Edition*. 
    Addison Wesley, 2003, ISBN 0201432943. 
    See also http://java.sun.com/docs/books/vmspec/2nd-edition/html/VMSpecTOC.doc.html.

.. [ENGEL] Engel, Joshua. 
    *Programming For The Java Virtual Machine*. 
    Addison Wesley, 1999. ISBN 0-201-30972-6.

.. [ARNOLD] Arnold, Ken, Gosling, James and Holmes, David. 
    *The Java Programming Language, Fourth Edition*. 
    Addison Wesley, 2009. ISBN 0-321-34980-6. 

.. [COFFEY] www.jamex.com. Neil Coffey.
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

.. [GREENWOOD] http://github.com/ToddG/experimental/java/concurrency

