//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose :
//*
//*
//*  Comments: This file uses a tab size of 3 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core

import utilities.subscript

//****************************************************************************

class Pitch(val midi : Midi) extends AnyVal
                                with Ordered[Pitch]
{
   def note      : Note                   = Note(this)
   def octave    : Octave                 = midi/12 - 1
   def frequency : Frequency              = Frequency(this)

   override def compare(p : Pitch)        = midi - p.midi
   override def toString()                = subscript(s"$note$octave")
}

object Pitch
{
   def apply(m : Midi)                    = new Pitch(m)
   def apply(f : Frequency)               = f.pitch
   def apply(n : Note,o : Octave)         = n.apply(o)

   implicit object ι extends Intervallic[Pitch]
   {
      def apply(p : Pitch,i : ℤ)          = new Pitch(p.midi + i)
      def delta(p : Pitch,q : Pitch)      = q.midi - p.midi
   }
}

//****************************************************************************
