//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Represents an even tempered pitch as the interval it forms with
//*            a standard reference pitch.
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package core

import util.utilities.subscript

/**
 * Represents an even tempered pitch as the interval it forms with a standard
 * reference pitch.
 *
 * A₄₄₀ (concert pitch) would seem to be the obvious choice for the reference,
 * however the arithmetic for calculating octaves, notes, frequencies,  and so
 * on, works out slightly simpler if we adopt the MIDI convention of using C₋₁
 * (8.18Hz) as pitch number 0 instead.
 *
 * The even tempered pitches are [[Intervallic intervallic]] in the sense that
 * they form a torsor for ℤ(0,+),  the set of integers viewed as a group under
 * addition, via the group action:
 * {{{
 *    Pitch(p) + i  =  Pitch(p + i)
 * }}}
 * for all ''p'' and ''i'' in ℤ. Each pair of pitches ''(p, q)'' identifies an
 * ''interval'',  the unique integer that when  applied to ''p'' transposes it
 * to ''q''. Moreover, this definition of interval as the delta function for a
 * torsor coincides with the more familiar musical notion of interval -  hence
 * the name.
 *
 * @param  midi  The note number assigned to this pitch by the MIDI Tuning Standard.
 *
 * @see    [[https://en.wikipedia.org/wiki/Equal_temperament Equal temperament (Wikipedia)]]
 * @see    [[https://en.wikipedia.org/wiki/A440_(pitch_standard) A440 (pitch standard) (Wikipedia)]]
 * @see    [[https://en.wikipedia.org/wiki/MIDI_Tuning_Standard MIDI Tuning Standard (Wikipedia)]]
 *
 * @author Jonathon Bell
 */
final class Pitch private (val midi: Midi) extends AnyVal
{
  /**
   * The pitch class, or ''note'', assigned to this pitch in scientific pitch
   * notation.
   *
   * Notice that for any pitch ''p'' and integer ''i'' we have that:
   * {{{
   *    (p + i).note  =  p.note + i
   * }}}
   * In other words, `note` is an equivariant mapping from the Pitch and Note
   * ℤ-sets.
   *
   * @see    [[https://en.wikipedia.org/wiki/Pitch_class Pitch class (Wikipedia)]]
   * @see    [[https://en.wikipedia.org/wiki/Scientific_pitch_notation Scientific pitch notation (Wikipedia)]]
   * @see    [[https://en.wikipedia.org/wiki/Equivariant_map Equivarient map (Wikipedia)]]
   */
  def note: Note = Note(this)

  /**
   * The octave number assigned to this pitch in scientific pitch notation.
   *
   * @see    [[https://en.wikipedia.org/wiki/Scientific_pitch_notation Scientific pitch notation (Wikipedia)]]
   */
  def octave: Octave = midi/12 - 1

  /**
   * The frequency of this pitch in hertz.
   *
   * @see    [[https://en.wikipedia.org/wiki/Frequency Frequency (Wikipedia)]]
   */
  def frequency: Frequency = Frequency(this)

  /**
   * Returns the sequence of pitches  ranging from  this to ''end'' inclusive,
   * and skipping ''step''  half-steps between each consecutive element of the
   * resulting range.
   *
   * @param  end   The bounding element of the resulting range.
   * @param  step  The number of half-steps between consecutive elements of the
   *               resulting range.
   *
   * @return The range `[this, this + step, ... , end]`
   */
  def to(end: Pitch,step: ℤ = +1): Seq[Pitch] =
  {
    (midi to end.midi).map(Pitch(_))
  }

  /**
   * Returns the sequence of pitches  ranging from  this to ''end'' exclusive,
   * and skipping ''step''  half-steps between each consecutive element of the
   * resulting range.
   *
   * @param  end   The bounding element of the resulting range.
   * @param  step  The number of half-steps between consecutive elements of the
   *               resulting range.
   *
   * @return The range `[this, this + step, ... , end)`
   */
  def until(end: Pitch,step: ℤ = +1): Seq[Pitch] =
  {
    (midi until end.midi by step).map(Pitch(_))
  }

  /**
   * The name assigned to this pitch in scientific pitch notation.
   *
   * @see [[https://en.wikipedia.org/wiki/Scientific_pitch_notation Scientific pitch notation (Wikipedia)]]
   */
  override
  def toString() = subscript(s"$note$octave")
}

/**
 * The companion object for class [[Pitch]].
 *
 * @author Jonathon Bell
 */
object Pitch
{
  /**
   * Returns the pitch specified by the given MIDI note number.
   *
   * @see    [[https://en.wikipedia.org/wiki/MIDI_Tuning_Standard MIDI Tuning Standard (Wikipedia)]]
   */
  def apply(midi: Midi): Pitch = new Pitch(midi)

  /**
   * Rounds the given frequency to the nearest even tempered pitch.
   */
  def apply(frequency: Frequency): Pitch = frequency.pitch

  /**
   * Returns the pitch specified by the given note and octave number in
   * scientific pitch notation.
   *
   * @see    [[https://en.wikipedia.org/wiki/Scientific_pitch_notation Scientific pitch notation (Wikipedia)]]
   */
  def apply(note: Note,octave: Octave): Pitch = note(octave)

  /**
   * TODO
   */
  implicit
  val `Finite[Pitch]` = new Finite[Pitch]
  {
    val size: ℕ                 = 128
    def toℕ(pitch: Pitch): ℕ    = pitch.midi
    def fromℕ(midi: ℕ): Pitch   = {require(midi.isBetween(0,127));Pitch(midi)}
  }

  /**
   * Pitches are ordered by their pitch number.
   */
  implicit
  val `Ordering[Pitch]` = new Ordering[Pitch]
  {
    def compare(p: Pitch,q: Pitch): ℤ  = p.midi - q.midi
  }

  /**
   * The additive group of integers ℤ acts upon the pitches via transposition
   * in half-steps.
   */
  implicit
  val `ℤTorsor[Pitch]` = new ℤTorsor[Pitch]
  {
    def apply(p: Pitch,i: ℤ): Pitch   = new Pitch(p.midi + i)
    def delta(p: Pitch,q: Pitch): ℤ   = q.midi - p.midi
  }

  /**
   * TODO
   */
  implicit
  val `CanBuildFrom[Set[_],Pitch,Pitches]` = FiniteSet.canBuildFrom[Pitch]

  /**
   * TODO
   */
  implicit
  val `PartialOrdering[Pitch]` = FiniteSet.partialOrdering[Pitch]

  /**
   * TODO
   */
  implicit
  val `ℤSet[Set[Pitch]]` = `ℤTorsor[Pitch]`.lift[Set]
}

/**
 * TODO
 *
 * @author Jonathon Bell
 */
object Pitches extends FiniteSet.Factory[Pitch]
{
  private[core]
  def fromBitMask(lo: Long,hi: Long): Pitches = FiniteSet.fromBitMask(Array(lo,hi))
}

//****************************************************************************

