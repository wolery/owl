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
//*
//*
//****************************************************************************

package com.wolery.owl.core

import Frequency._
import Math.{log,pow,abs,round}

//****************************************************************************

final class Frequency(val Hz: ℝ) extends Ordered[Frequency]
{
  require(Hz > 0)

  def kHz   : ℝ                           = Hz / 1e3
  def pitch : Pitch                       = A4 + round(A440 ⊣ this).toInt

  override def equals(a: Any)             = a match
  {
    case f: Frequency ⇒ close(f)
    case _            ⇒ false
  }

  override def compare(f: Frequency)      = if (close(f)) 0 else Hz compare f.Hz
  override def toString()                 = if (kHz >= 1) f"$kHz%.2fkHz" else  f"$Hz%.2fHz"
  private  def close(f: Frequency)        = abs(this ⊣ f) < 1e-2
}

object Frequency
{
  def apply(r: ℝ)                         = new Frequency(r)
  def apply(p: Pitch)                     = A440 + (A4 ⊣ p).toDouble

  private val A4                          = Pitch(A,4)
  private val A440                        = Hz(440.0)

  implicit val τ: Torsor[Frequency,ℝ]     = new Torsor[Frequency,ℝ]
  {
     val zero                             = 0.0
     def negate(r: ℝ)                     = -r
     def plus  (r: ℝ,s: ℝ)                = r + s
     def apply (f: Frequency,r: ℝ)        = Hz (pow(2,r/12.0) * f.Hz)
     def delta (f: Frequency,g: Frequency)= log(g.Hz / f.Hz) / log_α
     val log_α                            = log(pow(2,1/12.0))
  }
}

//****************************************************************************
