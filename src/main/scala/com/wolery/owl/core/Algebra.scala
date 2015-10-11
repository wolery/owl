//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Describes the algebraic constructions as type classes.
//*
//*
//*  Comments: This file uses a tab size of 3 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core

/**
 *	Describes the operations that endow the set G with a group structure.
 *
 * @see	https://en.wikipedia.org/wiki/Group_(mathematics)
 */
trait Group[G]
{
   def zero                 : G
   def negate(a : G)        : G
   def plus  (a : G,b : G)  : G
}

trait Action[S,G] extends Group[G]
{
   def apply(s : S,g : G)   : S
}

trait Torsor[S,G] extends Action[S,G]
{
   def delta(s : S,t : S)   : G
}

trait Transposing[S] extends Action[S,ℤ]
{
   def zero                 : ℤ = 0
   def negate(a : ℤ)        : ℤ = -a
   def plus  (a : ℤ,b : ℤ)  : ℤ = a + b
}

trait Intervallic[S] extends Torsor[S,ℤ]
{
   def zero                 : ℤ = 0
   def negate(a : ℤ)        : ℤ = -a
   def plus  (a : ℤ,b : ℤ)  : ℤ = a + b
}

//****************************************************************************
