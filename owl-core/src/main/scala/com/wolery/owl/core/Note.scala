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
 * @author Jonathon Bell
 *
 * https://en.wikipedia.org/wiki/Pitch_class
 */
final
class Note private (private val m_imp: ℕ) extends AnyVal
{
  def apply(octave: Octave): Pitch =
  {
    Pitch(octave*12 +12 + m_imp)
  }

  override
  def toString: String =
  {
    val i = 2 * m_imp                                    //

    "C C♯D D♯E F F♯G G♯A A♯B ".substring(i,i + 2).trim   //
  }
}

/**
 * TODO
 *
 * @author Jonathon Bell
 */
object Note
{
  def apply(pitch: Pitch): Note =
  {
    new Note(mod12(pitch.midi))
  }

  implicit
  object isFinite extends Finite[Note]
  {
    val size              = 12
    def toℕ(note: Note) = note.m_imp
    def fromℕ(n: ℕ)      =
    {
      require(n.isBetween(0,11))
      new Note(n)
    }
  }

  implicit
  object isℤTorsor extends Torsor[Note,ℤ]
  {
    def apply(n: Note,i: ℤ)               = new Note(mod12(n.m_imp + i))
    def delta(m: Note,n: Note)            = mod12(n.m_imp - m.m_imp)
  }

// Finite Set support

  implicit
  object canBuildFrom extends FiniteSet.CanBuildFrom[Note]

  implicit
  object isPartiallyOrdered extends FiniteSet.isPartiallyOrdered[Note]

  implicit
  object isℤSet extends Action[Notes,ℤ]
  {
/// def apply(s: Notes,i: ℤ)              = new Notes(rol12(s.bits,i))
    def apply(s: Notes,i: ℤ)              = s.map(_ + i)
  }
}

//****************************************************************************
//https://en.wikipedia.org/wiki/Set_theory_(music)
object Notes extends FiniteSet.Factory[Note]
{
  private[core]
  def fromBitMask(mask: Long): Pitches = FiniteSet.fromBitMask(Array(mask))
}

//****************************************************************************
