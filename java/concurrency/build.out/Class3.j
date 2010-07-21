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

