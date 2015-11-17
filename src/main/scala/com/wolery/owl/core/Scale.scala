//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Represents a musical scale as a set of notes, together with its
//*            distinguished element, the 'root' of the scale.
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core

/**
 * Represents a musical scale as a subset of the twelve notes, together with a
 * distinguished element, the ''root'' of the scale.
 *
 * @param root  The tonic center, or ''root'' note, of the scale.
 * @param shape The underlying interval structure, or ''shape'', of the scale.
 *
 * @see [[https://en.wikipedia.org/wiki/Scale_(music)]]
 */
final case class Scale (val root: Note,val shape: Shape)
{
  def size: ℕ                             = shape.size

  def notes: Notes                        = (Notes.empty /: shape.intervals)((s,i) ⇒ s + (root + i))
  def toSet: Notes                        = notes
  def toSeq: Seq[Note]                    = shape.absolute.map(root + _)

  def mode(i: ℤ): Scale                   = Scale(note(i),toSeq:_*)
  def modes: Seq[Scale]                   = toSeq.map(Scale(_,toSeq:_*))

  override def toString: String           = s"$root $shape"

  def note(i: ℤ): Note                    = root + shape.interval(i)
  def contains(n: Note): Bool             = shape.intervals.contains(n - root)
  def indexOf(n: Note): Maybe[ℕ]          = shape.indexOf(n - root)
}

//****************************************************************************

object Scale
{
  def apply(r: Note,n: Name): Maybe[Scale]= Shape(n).map(Scale(r,_))
  def apply(r: Note,s: Note*): Scale      = Scale(r,Notes(s:_*))
  def apply(r: Note,s: Set[Note]): Scale  = Scale(r,Shape(s.map(_-r)))

  implicit object transposing extends Transposing[Scale]
  {
    def apply(s: Scale,i: ℤ): Scale       = new Scale(s.root + i,s.shape)
  }
}

//****************************************************************************
