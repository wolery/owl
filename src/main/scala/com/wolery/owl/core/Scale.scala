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

final class Scale private (val root: Note,val shape: Shape)
{
  def aliases: Seq[String]                = ???
  def size: ℕ                             = shape.size

  def notes: Notes                        = (Notes.empty /: shape.intervals)((s,i) ⇒ s + (root + i))

  def mode(mode: ℕ): Scale                = ???
  def modes: Seq[Scale]                   = ???

  override def toString: String           = s"$root $shape"
}

//****************************************************************************

object Scale
{
  def apply(r: Note,s: Note*): Scale      = new Scale(r,Shape(0,s.map(_-r):_*))
  def apply(n: Note,s: Shape): Scale      = new Scale(n,s)

  implicit object transposing extends Transposing[Scale]
  {
    def apply(s: Scale,i: ℤ): Scale       = new Scale(s.root + i,s.shape)
  }
}

//****************************************************************************
