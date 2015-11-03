//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Represents a set of notes as bit set.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core;

//****************************************************************************

import java.lang.Integer._
import scala.collection.SetLike
import scala.collection.mutable.Builder
import scala.collection.generic.CanBuildFrom

import Notes._
import utilities._

//****************************************************************************

final class Notes private (private val bits: Int) extends Set[Note]
                                                     with SetLike[Note,Notes]
{
  assert((bits & ~0xFFF) == 0,"bad bits")

  def iterator: Iterator[Note]            = new Iterator[Note]
  {
    var i = 0

    def hasNext: Bool                     = (1 << i) <= highestOneBit(bits)

    def next(): Note =
    {
      require(hasNext);
      i = i + numberOfTrailingZeros(bits >> i) + 1
      C + i - 1
    }
  }

  def +  (n: Note) : Notes                = new Notes(bits |  bit(n))
  def -  (n: Note) : Notes                = new Notes(bits & ~bit(n))
  def unary_~      : Notes                = new Notes(0xFFF& ~ bits)

  def contains(n: Note):  Bool            = (bits   & bit(n))  != 0

  override def size: ℕ                    = bitCount(bits)
  override def empty: Notes               = new Notes(0)
  override def newBuilder: Builder[Note,Notes] = builder
}

//****************************************************************************

object Notes
{
  def empty: Notes                        = new Notes(0)
  def apply(s: Note*)                     = (builder ++= s).result
  def apply(s: Set[Note])                 = (builder ++= s).result

  implicit
  object canBuildFrom extends CanBuildFrom[Notes,Note,Notes]
  {
    def apply()                           = builder
    def apply(s: Notes)                   = builder
  }

  implicit
  object transposing extends Transposing[Notes]
  {
    def apply(s: Notes,i: ℤ)              = new Notes(rol12(s.bits,i))
  }

  private
  def builder: Builder[Note,Notes]        = new Builder[Note,Notes]
  {
    var bits: Int                         = 0
    def += (n: Note)                      = {bits|= bit(n); this}
    def clear()                           = {bits = 0 }
    def result()                          = new Notes(bits)
  }

  private
  def bit(n: Note): Int                   = 1 << n-C

  private[core]
  def apply(bits: Int)                    = new Notes(bits)
}

//****************************************************************************
