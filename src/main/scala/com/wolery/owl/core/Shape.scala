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

import java.lang.Integer.{bitCount,numberOfTrailingZeros ⇒ ntz}
import scala.collection.immutable.BitSet
import utilities.{mod12,ror12}

//****************************************************************************

final class Shape private (val bits: Bits) extends AnyVal
{
  def aliases: Seq[Name]                  = Shapes(bits) match
  {
    case None    ⇒ Nil
    case Some(i) ⇒ i.aliases
  }

  def size: ℕ                             = bitCount(bits)

  def intervals: BitSet                   = new BitSet.BitSet1(bits)

  def mode(mode: ℤ): Shape                =
  {
    def mod(i: ℤ): ℕ =
    {
      val m = i % size;

      if (m < 0) m + size else m
    }

    var n: ℕ = 0

    for (i ← 0 until mod(mode))
    {
      n += 1 + ntz(bits >>> (n+1))
    }

    new Shape(ror12(bits,n))
  }

  def modes: Seq[Shape]                   = for (m ← 1 to size) yield mode(m)

  def scale(root: Note): Scale            = Scale(root,this)
  def apply(root: Note): Scale            = Scale(root,this)

  override def toString: String           = Shapes(bits).map(_.name).getOrElse(intervals.mkString("Shape(",", ",")"))
}

//****************************************************************************

object Shape
{
  def apply(n: Name): Maybe[Shape]        = Shapes(n).map(_.shape)

  def apply(head: ℤ,tail: ℤ*): Shape = head match
  {
    case 0 ⇒ {           new Shape((1          /: tail)((b,i) ⇒ b | bit(i)))}
    case h ⇒ {var n = h; new Shape(((1|bit(h)) /: tail)((b,i) ⇒ b | bit{n+=i;n}))}
  }

  private
  def bit(interval: ℤ): Int               = 1 << mod12(interval)
}

//****************************************************************************
