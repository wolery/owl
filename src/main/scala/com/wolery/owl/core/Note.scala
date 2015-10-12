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

import utilities.mod12

//****************************************************************************

class Note private(private val n: ℤ) extends AnyVal
{
  def apply(o: Octave)        = new Pitch(n + (o+1)*12)

  override def toString()     = Array("C","C♯","D","D♯","E","F","F♯","G","G♯","A","A♯","B").apply(n)
}

//****************************************************************************

object Note
{
  def apply(p: Pitch)         = new Note(mod12(p.midi))

  val notes: Seq[Note]        = for (i ← 0 to 11) yield new Note(i)

  implicit object ι extends Intervallic[Note]
  {
    def apply(n: Note,i: ℤ)   = new Note(mod12(n.n + i))
    def delta(m: Note,n: Note)= mod12(n.n - m.n)
  }
}

//****************************************************************************
