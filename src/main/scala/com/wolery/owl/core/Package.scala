//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose :
//*
//*
//*  Comments: This file uses a tab size of 3 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl

//****************************************************************************

package object core
{
   type ℤ                                 = Int
   type ℝ                                 = Double
   type Bool                              = Boolean
   type Midi                              = Int
   type Octave                            = Int

   def Hz (r : ℝ) : Frequency             = new Frequency(r)
   def kHz(r : ℝ) : Frequency             = new Frequency(r * 1e3)

   val C : Note                           = Note.notes(0)
   val D : Note                           = Note.notes(2)
   val E : Note                           = Note.notes(4)
   val F : Note                           = Note.notes(5)
   val G : Note                           = Note.notes(7)
   val A : Note                           = Note.notes(9)
   val B : Note                           = Note.notes(11)

//****************************************************************************

   implicit final class BooleanSyntax(a : Bool)
   {
      def implies(b : Bool) : Bool        = b || !a
      def iff    (b : Bool) : Bool        = a == b
   }

   implicit final class GroupSyntax[G](f : G)(implicit α : Group[G])
   {
      def unary_+   : G                   = f
      def unary_-   : G                   = α.negate(f)
      def + (g : G) : G                   = α.plus(f, g)
      def - (g : G) : G                   = α.plus(f,-g)
   }

   implicit final class ActionSyntax[S,G](s : S)(implicit α : Action[S,G])
   {
      def + (g : G) : S                   = α.apply(s, g)
      def - (g : G) : S                   = α.apply(s,-g)
   }

   implicit final class TorsorSyntax[S,G](s : S)(implicit α : Torsor[S,G])
   {
      def ⊣ (t : S) : G                   = α.delta(s,t)
      def ⊢ (t : S) : G                   = α.delta(t,s)
   }

   implicit final class TransposingSyntax[S](s : S)(implicit α : Action[S,ℤ])
   {
      def ♭                               = α.apply(s,-1)
      def ♮                               = s
      def ♯                               = α.apply(s,1)
   }

//****************************************************************************

   implicit object Torsorℤℤ extends Torsor[ℤ,ℤ]
   {
      def zero            : ℤ             = 0
      def negate(i:ℤ)     : ℤ             = -i
      def plus  (i:ℤ,j:ℤ) : ℤ             = i + j
      def apply (i:ℤ,j:ℤ) : ℤ             = i + j
      def delta (i:ℤ,j:ℤ) : ℤ             = j - i
   }

   implicit object Torsorℝℝ extends Torsor[ℝ,ℝ]
   {
      def zero            : ℝ             = 0
      def negate(i:ℝ)     : ℝ             = -i
      def plus  (i:ℝ,j:ℝ) : ℝ             = i + j
      def apply (r:ℝ,s:ℝ) : ℝ             = r + s
      def delta (r:ℝ,s:ℝ) : ℝ             = s - r
   }
}

//****************************************************************************
