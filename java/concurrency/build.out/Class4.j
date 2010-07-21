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

