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

//****************************************************************************

import utilities.subscript

//****************************************************************************

final class Pitch private (val midi: Midi) extends AnyVal
{
  def note     : Note                     = Note(this)
  def octave   : Octave                   = midi/12 - 1
  def frequency: Frequency                = Frequency(this)

  override def toString()                 = subscript(s"$note$octave")
}

object Pitch
{
  def apply(m: Midi): Pitch               = new Pitch(m)
  def apply(f: Frequency): Pitch          = f.pitch
  def apply(n: Note,o: Octave): Pitch     = n.apply(o)

  implicit object ordering extends Ordering[Pitch]
  {
    def compare(p: Pitch,q: Pitch)        = p.midi - q.midi
  }

  implicit object intervallic extends Intervallic[Pitch]
  {
    def apply(p: Pitch,i: ℤ)              = new Pitch(p.midi + i)
    def delta(p: Pitch,q: Pitch)          = q.midi - p.midi
  }
}

//****************************************************************************
