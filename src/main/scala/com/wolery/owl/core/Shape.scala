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
import utilities.{mod,mod12,ror12}

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

  def absolute: Seq[ℤ]  =
  {
    var n: ℕ = 0

    for (i ← 0 until size) yield
    {
      val m = n

      n += 1 + ntz(bits >>> n+1)

      m
    }
  }

  def relative: Seq[ℤ]  =
  {
    var n: ℕ = 0

    for (i ← 1 until size) yield
    {
      val m = n

      n += 1 + ntz(bits >>> n+1)

      n - m
    }
  }

  def mode(i: ℤ): Shape                   = new Shape(ror12(bits,interval(i)))

  def interval(index: ℤ): ℤ =
  {
    var i: ℕ = mod(index,size)
    var n: ℕ = 0

    while (i > 0)
    {
      n += 1 + ntz(bits >>> n+1)
      i -= 1
    }
    n
  }

  def modes: Seq[Shape] =
  {
    var n: ℕ = 0

    for (i ← 0 until size) yield
    {
      val m = n

      n += 1 + ntz(bits >>> n+1)

      Shape(ror12(bits,m))
    }
  }

  override def toString: String           = Shapes(bits).map(_.name).getOrElse(intervals.mkString("Shape(",", ",")"))
}

//****************************************************************************

object Shape
{
  def apply(n: Name): Maybe[Shape]        = Shapes(n).map(_.shape)

  def apply(intervals: ℤ*): Shape = intervals match
  {
    case Seq()       ⇒ {        Shape(1)}
    case Seq(0,t@_*) ⇒ {        Shape((1 /: t)((b,i) ⇒ b | bit(i)))}
    case s           ⇒ {var n=0;Shape((1 /: s)((b,i) ⇒ b | bit{n+=i;n}))}
  }

  def apply(intervals: Set[ℤ]): Shape     = Shape((1 /: intervals)((b,i) ⇒ b | bit(i)))

  implicit object transposing extends Transposing[Shape]
  {
    def apply(s: Shape,i: ℤ)              = s mode i
  }

  private
  def bit(interval: ℤ): Bits              = 1 << mod12(interval)

  private[core]
  def apply(bits: Bits): Shape =
  {
    assert((bits & ~0xFFF) == 0,"extraneous bits")
    assert((bits &  0x001) == 1,"must include 0")
    new Shape(bits)
  }
}

//****************************************************************************
