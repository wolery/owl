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
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package core

//****************************************************************************

import Math.{log,pow,abs,round}
import Frequency.{A4,A440}

/**
 * Represents an audio frequency as a positive real number of hertz.
 *
 * Frequencies form a torsor for ℝ(0,+), the set of real numbers regarded as a
 * group under addition, via the group action:
 * {{{
 *    f Hz + r  =  pow(2,r/12) * f Hz
 * }}}
 * for all ''f'' and ''r'' in ℝ. Each pair of frequencies ''(f,g)'' identifies
 * an ''interval'',  the unique real that  when applied to ''f'' transposes it
 * to ''g''. Moreover, this definition of interval as the delta function for a
 * torsor coincides with the more familiar notion of musical interval -  hence
 * the name.
 *
 * Informally, this action captures the notion of ''transposition'',  with the
 * real number ''r'' representing a (possibly fractional) number of half-steps
 * to raise or lower the frequency ''f'' by. Notice, for example, that:
 * {{{
 *    f + 12  =  2 * f
 * }}}
 * for any frequency ''f'', which corresponds to the transposition of ''f'' by
 * an interval of one octave.
 *
 * @param  Hz  The underlying frequency in hertz.
 *
 * @see   [[https://en.wikipedia.org/wiki/Hertz Hertz (Wikipedia)]]
 * @see   [[https://en.wikipedia.org/wiki/Frequency Frequency (Wikipedia)]]
 */
final class Frequency private (val Hz: ℝ)
{
  assert(Hz > 0,"non-positive Hz")

  /**
   * The underlying frequency in kilohertz.
   */
  def kHz: ℝ = Hz * 1e-3

  /**
   * Rounds this frequency to the nearest even tempered pitch.
   */
  def pitch: Pitch = A4 + round(this - A440).toInt

  /**
   * True if ''any'' is a frequency that sounds within one cent of a half-step
   * of this one.
   */
  override
  def equals(any: Any) = any match
  {
    case f: Frequency ⇒ close(f)
    case _            ⇒ false
  }

  /**
   * A formatted string representation of this frequency.
   */
  override
  def toString: String = if (kHz >= 1) f"$kHz%.2fkHz" else f"$Hz%.2fHz"

  /**
   * True if the given frequency is sounds within once cent of a half-step of
   * this one.
   */
  private
  def close(that: Frequency): Bool = abs(this - that) < 1e-2
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
   * Returns the underlying frequency of an even tempered pitch.
   */
  def apply(pitch: Pitch): Frequency = A440 + (pitch - A4).toDouble

  /**
   * Frequencies are ordered by the underlying real number that they wrap, but
   * compare equal when within one cent of a half-step of one another.
   *
   * Note that this behavior is not quite consistent with the implementation
   * of [[hashCode]] they currently inherit.
   */
  implicit
  object ordering extends Ordering[Frequency]
  {
    def compare(f: Frequency,g: Frequency)= if (f close g) 0 else f.Hz compare g.Hz
  }

  /**
   * Frequencies are acted upon by the reals via transposition in half-steps.
   */
  implicit
  object isℝTorsor extends ℝTorsor[Frequency]
  {
     def apply (f: Frequency,r: ℝ)        = Hz (pow(2,r/12.0) * f.Hz)
     def delta (f: Frequency,g: Frequency)= log(g.Hz / f.Hz) / log_α
     val log_α                            = log(pow(2,1/12.0))
  }

  private val A4                          = Pitch(A,4)
  private val A440                        = Hz(440.0)
}

//****************************************************************************
