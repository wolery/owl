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
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package core

/**
 * Represents a musical scale as a subset of the twelve notes, together with a
 * distinguished element, the ''root'' of the scale.
 *
 * @param  root   The tonic center, or ''root'' note, of the scale.
 * @param  shape  The underlying interval structure, or ''shape'', of the scale.
 *
 * @see   [[https://en.wikipedia.org/wiki/Scale_(music) Scale (Wikipedia)]]
 */
final case class Scale (val root: Note,val shape: Shape)
{
  def name:  Option[Name]                 = shape.name .map(n ⇒ s"$root $n")
  def names: Seq[Name]                    = shape.names.map(n ⇒ s"$root $n")

  def size:  ℕ                            = shape.size
  def toSet: Notes                        = (Notes.empty /: shape.toSet)((s,i) ⇒ s + (root + i))
  def toSeq: Seq[Note]                    = shape.toSeq.map(root + _)

  def mode(mode: ℤ): Scale                = Scale(note(mode),toSet)
  def modes:         Seq[Scale]           = toSeq.map(Scale(_,toSet))

  def note    (index: ℤ):   Note          = root + shape.interval(index)
  def indexOf (note: Note): Option[ℕ]     = shape.indexOf (note - root)
  def contains(note: Note): Bool          = shape.contains(note - root)

  override def toString: String           = name.getOrElse(toSeq.mkString("Scale(",", ",")"))
}

//****************************************************************************

object Scale
{
  def apply(r: Note,n: Name): Option[Scale]= Shape(n).map(Scale(r,_))
  def apply(r: Note,s: Note*): Scale      = Scale(r,Notes(s:_*))
  def apply(r: Note,s: Traversable[Note]): Scale  = Scale(r,Shape(s.map(_-r)))

  /**
   * The integers act upon the set of scales via transposition.
   */
  implicit object isℤSpace extends ℤSpace[Scale]
  {
    def apply(s: Scale,i: ℤ): Scale       = new Scale(s.root + i,s.shape)
  }
}

//****************************************************************************
