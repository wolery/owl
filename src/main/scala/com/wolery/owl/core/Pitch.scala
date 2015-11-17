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
//*
//*
//****************************************************************************

package com.wolery.owl.core

//****************************************************************************

import utilities.subscript

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
 * 		Pitch(p) + i = Pitch(p + i)
 * }}}
 * for all ''p'' and ''i'' in ℤ. Each pair of pitches ''(p, q)'' identifies an
 * ''interval'',  the unique integer that when applied to ''p'' ''transposes''
 * it to ''q''. Moreover, this definition of interval as the delta function of
 * a torsor coincides with the more familiar notion of musical interval, hence
 * the name.
 *
 * @param midi The note number assigned this pitch by the MIDI Tuning Standard.
 * @see   [[https://en.wikipedia.org/wiki/Equal_temperament Equal Temperament]]
 * @see   [[https://en.wikipedia.org/wiki/A440_(pitch_standard) A440 (pitch standard)]]
 * @see   [[https://en.wikipedia.org/wiki/MIDI_Tuning_Standard MIDI Tuning Standard]]
 */
final class Pitch private (val midi: Midi) extends AnyVal
{
  /**
   * The pitch class, or ''note'', assigned this pitch in scientific pitch
   * notation.
   *
   * @see [[https://en.wikipedia.org/wiki/Pitch_class Pitch Class]]
   * @see [[https://en.wikipedia.org/wiki/Scientific_pitch_notation Scientific Pitch Notation]]
   */
  def note: Note = Note(this)

  /**
   * The octave number assigned this pitch in scientific pitch notation.
   *
   * @see [[https://en.wikipedia.org/wiki/Scientific_pitch_notation Scientific Pitch Notation]]
   */
  def octave: Octave = midi/12 - 1

  /**
   * The frequency of this pitch in hertz.
   *
   * @see [[https://en.wikipedia.org/wiki/Frequency Frequency]]
   */
  def frequency: Frequency = Frequency(this)

  /**
   * The name assigned this pitch in scientific pitch notation.
   *
   * @see [[https://en.wikipedia.org/wiki/Scientific_pitch_notation Scientific Pitch Notation]]
   */
  override def toString() = subscript(s"$note$octave")
}

/**
 * The companion object for class [[Pitch]].
 */
object Pitch
{
  /**
   * Returns the pitch specified by the given MIDI note number.
   *
   * @see [[https://en.wikipedia.org/wiki/midi_tuning_standard MIDI Tuning Standard]]
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
   * @see [[https://en.wikipedia.org/wiki/Scientific_pitch_notation Scientific Pitch Notation]]
   */
  def apply(note: Note,octave: Octave): Pitch = note(octave)

  /**
   * Pitches are ordered by their pitch number.
   */
  implicit object ordering extends Ordering[Pitch]
  {
    def compare(p: Pitch,q: Pitch): ℤ = p.midi - q.midi
  }

  /**
   * The integers act on the pitches via addition of note numbers.
   */
  implicit object intervallic extends Intervallic[Pitch]
  {
    def apply(p: Pitch,i: ℤ): Pitch = new Pitch(p.midi + i)
    def delta(p: Pitch,q: Pitch): ℤ = q.midi - p.midi
  }
}

//****************************************************************************
