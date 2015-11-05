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

import java.lang.Integer.bitCount
import scala.collection.immutable.BitSet
import utilities.{mod12}
import Shape._

//****************************************************************************

final class Shape private (val bits: Bits) extends AnyVal
{
  def name: Name                          = Shapes(bits).map(_.name).getOrElse(toString)

  def aliases: Seq[Name]                  = Shapes(bits) match
  {
    case None    ⇒ Nil
    case Some(i) ⇒ i.aliases
  }

  def size: ℕ                             = bitCount(bits)

  def intervals: BitSet                   = new BitSet.BitSet1(bits)

  def mode(m: ℤ)                          = ???

  def modes: Seq[Shape]                   = for (m ← 1 to size) yield mode(m)

  def scale(root: Note): Scale            = ???
  def apply(root: Note): Scale            = scale(root)

  override def toString: String           = intervals.mkString("Shape(",", ",")")
}

object Shape
{
  def apply(n: Name): Maybe[Shape]        = Shapes(n).map(_.shape)

  def apply(notes: Notes): Shape          =
  {
    if (notes.isEmpty)
    {
      new Shape(0)
    }
    else
    {
      val h = notes.head
      new Shape((0 /: notes)((b,n) ⇒ b | bit(n - h)))
    }
  }

  def apply(intervals: ℤ*): Shape =
  {
    require(!intervals.isEmpty)

    var n = 0
    val b = if (intervals(0) == 0)
                (0 /: intervals)((b,i) ⇒ b | bit(i))
              else
                (1 /: intervals)((b,i) ⇒ b | bit({n+=i;n}))
    new Shape(b)
  }

//  private
//def Shape(b: Bits)                      = new Shape(b & 0xFFF)

  private
  def bit(interval: ℤ): Int               = 1 << mod12(interval)
}

//****************************************************************************
