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
package core

import util.utilities.mod12

/**
 * TODO
 *
 * @see    [[https://en.wikipedia.org/wiki/Pitch_class Pitch Class (Wikipedia)]]
 *
 * @author Jonathon Bell
 */
final class Note private (private val m_imp: ℕ) extends AnyVal
{
  /**
   * TODO
   *
   * @param  octave  TODO
   *
   * @return TODO
   */
  def apply(octave: Octave): Pitch =
  {
    Pitch(octave*12 +12 + m_imp)
  }

  /**
   * TODO
   *
   * @return TODO
   */
  override
  def toString: String =
  {
    val i = 2 * m_imp                                    // Index into literal

    "C C♯D D♯E F F♯G G♯A A♯B ".substring(i,i + 2).trim   // Index into literal
  }
}

/**
 * TODO
 *
 * @author Jonathon Bell
 */
object Note
{
  /**
   * TODO
   *
   * @param  pitch  TODO
   *
   * @return TODO
   */
  def apply(pitch: Pitch): Note =
  {
    new Note(mod12(pitch.midi))
  }

  /**
   * TODO
   */
  implicit
  val `Finite[Note]`: Finite[Note] = new Finite[Note]
  {
    val size           : ℕ     = 12
    def toℕ(note: Note): ℕ     = note.m_imp
    def fromℕ(i: ℕ)    : Note  = {require(i.isBetween(0,11));new Note(i)}
  }

  /**
   * TODO
   */
  implicit
  val `ℤTorsor[Note]`: ℤTorsor[Note] = new ℤTorsor[Note]
  {
    def apply(n: Note,i: ℤ)               = new Note(mod12(n.m_imp + i))
    def delta(m: Note,n: Note)            = mod12(n.m_imp - m.m_imp)
  }

  /**
   * TODO
   */
  implicit
  val `ℤSet[Set[Note]]`: ℤSet[Set[Note]] = `ℤTorsor[Note]`.lift[Set]

  /**
   * TODO
   */
  implicit
  val `ℤSet[Notes]`: ℤSet[Notes] = new ℤSet[Notes]
  {
    def apply(notes: Notes,i: ℤ)          = notes.map(_ + i)
  }
}

/**
 * TODO
 *
 * @see    [https://en.wikipedia.org/wiki/Set_theory_(music) Music Set Theory
 *         (Wikipedia)]
 *
 * @author Jonathon Bell
 */
object Notes extends FiniteSet.Factory[Note]
{
  /**
   * TODO
   *
   * @param  mask  TODO
   *
   * @return TODO
   */
  private[core]
  def fromBitMask(mask: Long): Notes = FiniteSet.fromBitMask(Array(mask))
}

//****************************************************************************
