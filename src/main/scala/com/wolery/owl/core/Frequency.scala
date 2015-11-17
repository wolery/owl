//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Represents an audio frequency as a positive real number.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core

//****************************************************************************

import Math.{log,pow,abs,round}
import Frequency.{A4,A440}

/**
 * Represents an audio frequency as a positive real number of hertz.
 *
 * @param Hz The underlying frequency as a positive real number of hertz.
 * @see   [[https://en.wikipedia.org/wiki/Hertz Hertz]]
 * @see   [[https://en.wikipedia.org/wiki/Frequency Frequency]]
 */
final class Frequency private (val Hz: ℝ)
{
  assert(Hz > 0,"non-positive Hz")

  /**
   * The underlying frequency as a positive real number of kilohertz.
   */
  def kHz: ℝ = Hz * 1e-3

  /**
   * Rounds this frequency to the nearest even tempered pitch.
   */
  def pitch: Pitch = A4 + round(this - A440).toInt

  /**
   * Return true if the given object is another frequency within one cent of
   * this one.
   */
  override def equals(any: Any) = any match
  {
    case f: Frequency ⇒ close(f)
    case _            ⇒ false
  }

  /**
   * Returns a formatted string representation of this frequency.
   */
  override def toString: String = if (kHz >= 1) f"$kHz%.2fkHz" else f"$Hz%.2fHz"

  /**
   * Returns true if the given frequency is within once cent of this one.
   */
  private def close(that: Frequency): Bool = abs(this - that) < 1e-2
}

/**
 * The companion object for class [[Frequency]].
 */
object Frequency
{
  /**
   * Returns the given frequency in hertz.
   */
  def apply(real: ℝ): Frequency = new Frequency(real)

  /**
   * Returns the underlying frequency of the given pitch.
   */
  def apply(pitch: Pitch): Frequency = A440 + (pitch - A4).toDouble

  /**
   *
   */
  implicit object ordering extends Ordering[Frequency]
  {
    def compare(f: Frequency,g: Frequency)= if (f close g) 0 else f.Hz compare g.Hz
  }

  /**
   *
   */
  implicit val torsor: Torsor[Frequency,ℝ]= new Torsor[Frequency,ℝ]
  {
     val zero                             = 0.0
     def negate(r: ℝ)                     = -r
     def plus  (r: ℝ,s: ℝ)                = r + s
     def apply (f: Frequency,r: ℝ)        = Hz (pow(2,r/12.0) * f.Hz)
     def delta (f: Frequency,g: Frequency)= log(g.Hz / f.Hz) / log_α
     val log_α                            = log(pow(2,1/12.0))
  }

  private val A4                          = Pitch(A,4)
  private val A440                        = Hz(440.0)
}

//****************************************************************************
