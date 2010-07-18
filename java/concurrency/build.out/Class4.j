.source Class4.java
.class public super Class4
.super java/lang/Object

.field private volatile i I

.method public <init> ()V
.limit stack 1
.limit locals 1
.var 0 is this LClass4; from l0 to l5
.line 1
l0:    aload_0
l1:    invokespecial java/lang/Object/<init> ()V
l4:    return

.end method

.method public getI ()I
.limit stack 1
.limit locals 1
.var 0 is this LClass4; from l0 to l5
.line 5
l0:    aload_0
l1:    getfield Class4/i I
l4:    ireturn

.end method

.method public setI (I)V
.limit stack 2
.limit locals 2
.var 0 is this LClass4; from l0 to l6
.var 1 is i I from l0 to l6
.line 9
l0:    aload_0
l1:    iload_1
l2:    putfield Class4/i I
.line 10
l5:    return

.end method

