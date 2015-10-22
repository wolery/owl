//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Represents an audio frequency as a real number of hertz.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core

import Math.{log,pow,abs,round}
import Frequency._

//****************************************************************************

final class Frequency(val Hz: ℝ) extends Ordered[Frequency]
{
  require(Hz > 0)

  def kHz: ℝ                              = Hz * 1e-3
  def pitch: Pitch                        = A4 + round(this - A440).toInt

  override def equals(a: Any)             = a match
  {
    case f: Frequency ⇒ close(f)
    case _            ⇒ false
  }

  override def toString                   = if (kHz >= 1) f"$kHz%.2fkHz" else f"$Hz%.2fHz"
  override def compare(f: Frequency)      = if (close(f)) 0 else Hz compare f.Hz
  private  def close(f: Frequency)        = abs(this - f) < 1e-2
}

object Frequency
{
  def apply(r: ℝ)                         = new Frequency(r)
  def apply(p: Pitch)                     = A440 + (p - A4).toDouble

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
