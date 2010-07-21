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

