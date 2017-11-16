//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose :
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl

//****************************************************************************

package object core
{
  type Bits                           = Int
  type Midi                           = Int
  type Octave                         = Int

  def Hz (r: ℝ): Frequency            = Frequency(r)
  def kHz(r: ℝ): Frequency            = Frequency(r * 1e3)

  val C: Note                         = Note(Pitch(0))
  val D: Note                         = Note(Pitch(2))
  val E: Note                         = Note(Pitch(4))
  val F: Note                         = Note(Pitch(5))
  val G: Note                         = Note(Pitch(7))
  val A: Note                         = Note(Pitch(9))
  val B: Note                         = Note(Pitch(11))

//****************************************************************************

  implicit final class MonoidEx[M](m: M)(implicit α: Monoid[M])
  {
    def ⋅ (n: M): M                   = α.operate(m,n)
    def + (n: M): M                   = α.operate(m,n)
    def unary_+ : M                   = m
  }

  implicit final class GroupEx[G](f: G)(implicit α: Group[G])
  {
    def - (g: G): G                   = α.operate(f,α.inverse(g))
    def unary_- : G                   = α.inverse(f)
  }

  implicit final class ActionEx[S,G](s: S)(implicit α: Action[S,G])
  {
    def + (g: G): S                   = α(s,g)
    def - (g: G): S                   = α(s,α.group.inverse(g))
  }

  implicit final class TorsorEx[S,G](s: S)(implicit α: Torsor[S,G])
  {
    def - (t: S): G                   = α.delta(t,s)
  }

  implicit final class ℤActionEx[S](s: S)(implicit α: Action[S,ℤ])
  {
    def ♭                             = α(s,-1)
    def ♮                             = s
    def ♯                             = α(s,+1)
  }
}

//****************************************************************************
