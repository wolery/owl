//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Represents a frequency as a positive real number of hertz.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package core

import Math.{log,pow,abs,round}
import Frequency.{A4,A440}

/**
 * Represents an audio frequency as a positive real number of hertz.
 *
 * Frequencies form a torsor for `ℝ(+)`, the set of real numbers regarded as a
 * group under addition, via the group action `+ = r ⇒ f ⇒ (¹²√2)`^`r`^ ` ⋅ f`
 * for all real numbers `r` and frequencies `f`.
 *
 * Informally,  this action captures the notion of ''transposition'', with `r`
 * representing a (possibly fractional) number of half-steps to raise or lower
 * the frequency `f` by. Notice for example that:
 * {{{
 *    f + 12  ≡  2 ⋅ f
 * }}}
 * for any frequency `f`, which corresponds to the transposition of `f` by one
 * octave.
 *
 * Each pair of frequencies `(f, g)` gives rise to an ''interval'', the unique
 * real number that when applied to `f` transposes it to `g`.  Moreover,  this
 * definition of interval - as the delta function of a torsor - coincides with
 * the familiar musical notion of the interval between two frequencies - hence
 * the name.
 *
 * @constructor Creates a frequency from a positive real number of cycles per
 *              second.
 *
 * @param  Hz  The underlying frequency in hertz.
 *
 * @see    [[https://en.wikipedia.org/wiki/Hertz Hertz (Wikipedia)]]
 * @see    [[https://en.wikipedia.org/wiki/Frequency Frequency (Wikipedia)]]
 * @author Jonathon Bell
 */
final class Frequency (val Hz: ℝ)
{
  require(Hz > 0,"non-positive Hz")                      // Validate argument

  /**
   * The underlying frequency in kilohertz.
   */
  def kHz: ℝ = Hz * 1e-3                                 // In thousands of Hz

  /**
   * Rounds this frequency to the nearest even tempered pitch.
   */
  def pitch: Pitch =
  {
    A4 + round(this - A440).toInt                        // The nearest pitch
  }

  /**
   * True if the given frequency sounds within one cent of a half-step of this
   * one.
   *
   * @param  any  Any object whatsoever.
   *
   * @return `true` if the given object is a frequency that sounds within one
   *         cent of a half-step of this one.
   */
  override
  def equals(any: Any) = any match
  {
    case f: Frequency ⇒ close(f)                         // A close frequency?
    case _            ⇒ false                            // No, I guess not...
  }

  /**
   * Returns a string representation of this frequency in hertz.
   */
  override
  def toString: String = if (kHz >= 1) f"$kHz%.2fkHz" else f"$Hz%.2fHz"

  /**
   * Returns `true` if the given frequency sounds within once cent of a half-
   * step of this one.
   *
   * Provides the underlying implementation of the `equals` function.
   */
  private
  def close(that: Frequency): Bool =
  {
    abs(this - that) < 1e-2                              // Interval < 0.01?
  }
}

/**
 * The companion object for class [[Frequency]].
 */
object Frequency
{
  /**
   * Creates a frequency from a positive real number of cycles per second.
   *
   * @param  hertz  The underlying frequency in hertz..
   *
   * @return TODO
   */
  def apply(hertz: ℝ): Frequency =
  {
    new Frequency(hertz)
  }

  /**
   * Returns the underlying frequency of an even tempered pitch.
   *
   * @param  pitch  An even tempered pitch.
   *
   * @return TODO
   */
  def apply(pitch: Pitch): Frequency =
  {
    A440 + (pitch - A4).toDouble
  }

  /**
   * Frequencies are ordered by the underlying real number that they wrap, but
   * compare equal when within one cent of a half-step of one another.
   *
   * Notice that this behavior is not quite consistent with the implementation
   * of [[hashCode]] they currently inherit.
   */
  implicit
  object isOrdering extends Ordering[Frequency]
  {
    def compare(f: Frequency,g: Frequency): ℤ =
    {
      if (f close g)
        0
      else
        f.Hz compare g.Hz
    }
  }

  /**
   * The reals `(ℝ,+)` act regularly upon the frequencies by transposition in
   * half-steps.
   */
  implicit
  object isℝTorsor extends Torsor[Frequency,ℝ]
  {
    def apply(f: Frequency,r: ℝ)          = Hz (pow(2,r/12.0) * f.Hz)
    def delta(f: Frequency,g: Frequency)  = log(g.Hz / f.Hz) / log_α
  }

  private val A4       = Pitch(A,4)                      // Concert pitch
  private val A440     = Hz(440.0)                       // Concert pitch
  private val log_α    = log(pow(2,1/12.0))              // ln (¹²√2)
}

//****************************************************************************
