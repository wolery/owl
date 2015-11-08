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

  def toSet: Notes                        = (Notes.empty /: shape.intervals)((s,i) ⇒ s + (root + i))
  def toSeq: Seq[Note]                    = shape.absolute.map(root + _)

  def note(i: ℕ): Note                    = root + shape.interval(i)
  def mode(i: ℤ): Scale                   = Scale(note(i),toSeq:_*)
  def modes: Seq[Scale]                   = toSeq.map(Scale(_,toSeq:_*))

  override def toString: String           = s"$root $shape"
  override def hashCode: Bits             = 41 * (41+root.##) + shape.##
  override def equals(a: Any) = a match
  {
    case s: Scale ⇒ s.root==root && s.shape == shape
    case _        ⇒ false
  }
}

//****************************************************************************

object Scale
{
  def apply(r: Note,n: Name): Maybe[Scale]= Shape(n).map(Scale(r,_))
  def apply(r: Note,s: Shape): Scale      = new Scale(r,s)
  def apply(r: Note,s: Notes): Scale      = Scale(r,Shape(s.map(_-r)))
  def apply(r: Note,s: Note*): Scale      = Scale(r,Notes(s:_*))

  implicit object transposing extends Transposing[Scale]
  {
    def apply(s: Scale,i: ℤ): Scale       = new Scale(s.root + i,s.shape)
  }
}

//****************************************************************************
