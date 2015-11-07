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

import utilities.mod

//****************************************************************************

final class Scale private (val root: Note,val shape: Shape)
{
  def size: ℕ                             = shape.size

  def set: Notes                          = (Notes.empty /: shape.intervals)((s,i) ⇒ s + (root + i))
  def seq: Seq[Note]                      = shape.absolute.map(root + _)

  def mode(i: ℤ): Scale                   = Scale(note(i),seq:_*)
  def modes: Seq[Scale]                   = seq.map(Scale(_,seq:_*))

  override def toString: String           = s"$root $shape"

  def note(i: ℕ): Note                    = root + shape.interval(i)
}

//****************************************************************************

object Scale
{
  def apply(r: Note,s: Note*): Scale      = new Scale(r,Shape(0,s.map(_-r):_*))
  def apply(r: Note,s: Shape): Scale      = new Scale(r,s)
  def apply(r: Note,n: Name): Maybe[Scale]= Shape(n).map(Scale(r,_))

  implicit object transposing extends Transposing[Scale]
  {
    def apply(s: Scale,i: ℤ): Scale       = new Scale(s.root + i,s.shape)
  }
}

//****************************************************************************
