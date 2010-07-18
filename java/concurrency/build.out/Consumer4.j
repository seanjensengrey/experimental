.source Consumer4.java
.class public super Consumer4
.super java/lang/Object


.method public <init> ()V
.limit stack 1
.limit locals 1
.var 0 is this LConsumer4; from l0 to l5
.line 1
l0:    aload_0
l1:    invokespecial java/lang/Object/<init> ()V
l4:    return

.end method

.method public static main ([Ljava/lang/String;)V
.limit stack 0
.limit locals 1
.var 0 is args [Ljava/lang/String; from l0 to l4
.line 4
l0:    invokestatic Consumer4/getClass4 ()V
.line 5
l3:    return

.end method

.method private static getClass4 ()V
.limit stack 2
.limit locals 2
.var 0 is class4 LClass4; from l8 to l20
.var 1 is i I from l19 to l20
.line 8
l0:    new Class4
l3:    dup
l4:    invokespecial Class4/<init> ()V
l7:    astore_0
.line 9
l8:    aload_0
l9:    bipush 123
l11:    invokevirtual Class4/setI (I)V
.line 10
l14:    aload_0
l15:    invokevirtual Class4/getI ()I
l18:    istore_1
.line 11
l19:    return

.end method

.method private static getClass5 ()V
.limit stack 2
.limit locals 2
.var 0 is class5 LClass5; from l8 to l20
.var 1 is i I from l19 to l20
.line 14
l0:    new Class5
l3:    dup
l4:    invokespecial Class5/<init> ()V
l7:    astore_0
.line 15
l8:    aload_0
l9:    bipush 123
l11:    invokevirtual Class5/setI (I)V
.line 16
l14:    aload_0
l15:    invokevirtual Class5/getI ()I
l18:    istore_1
.line 17
l19:    return

.end method

