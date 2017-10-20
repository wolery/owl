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

//****************************************************************************

import util.utilities.mod12

//****************************************************************************

final class Note private (private val n: ℕ) extends AnyVal
{
  def apply(o: Octave)                    = Pitch(n + (o+1)*12)

  override def toString()                 = Note.names(n)
}

//****************************************************************************

object Note
{
  def apply(p: Pitch): Note               = new Note(mod12(p.midi))

  implicit object intervallic extends Intervallic[Note]
  {
    def apply(n: Note,i: ℤ)               = new Note(mod12(n.n + i))
    def delta(m: Note,n: Note)            = mod12(n.n - m.n)
  }

  private
  val names = Array("C","C♯","D","D♯","E","F","F♯","G","G♯","A","A♯","B")
}

//****************************************************************************
