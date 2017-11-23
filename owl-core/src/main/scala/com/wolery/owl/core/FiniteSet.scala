//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : A specialized implementation of Set[α] for finite types α.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl
package core

import scala.collection.{GenSet,SetLike}
import scala.collection.mutable.{Builder,BitSet ⇒ mBitSet}
import scala.collection.generic.{CanBuildFrom ⇒ CBF}
import scala.collection.immutable.BitSet

import FiniteSet._

/**
 * A specialized implementation of `Set[α]` for finite types `α`.
 *
 * @tparam α  An instance of the type class `Finite`.
 *
 * @author Jonathon Bell
 */
final
class FiniteSet[α] private (private val m_imp: BitSet)(implicit ε: Finite[α])
extends Set[α] with SetLike[α,FiniteSet[α]]
{
  assert(m_imp.isEmpty || m_imp.last<ε.size)

  def iterator: Iterator[α] = new Iterator[α]
  {
    val i             = m_imp.iterator
    def hasNext: Bool = i.hasNext
    def next() : α    = ε.fromℕ(i.next())
  }

  def + (a: α)           : FiniteSet[α]  = fromBitSet(m_imp +  ε.toℕ(a))
  def - (a: α)           : FiniteSet[α]  = fromBitSet(m_imp -  ε.toℕ(a))
  def \ (s: FiniteSet[α]): FiniteSet[α]  = fromBitSet(m_imp &~ s.m_imp)
  def ∪ (s: FiniteSet[α]): FiniteSet[α]  = fromBitSet(m_imp |  s.m_imp)
  def ∩ (s: FiniteSet[α]): FiniteSet[α]  = fromBitSet(m_imp &  s.m_imp)
  def ⊖ (s: FiniteSet[α]): FiniteSet[α]  = fromBitSet(m_imp ^  s.m_imp)
  def unary_~            : FiniteSet[α]  = fromBitSet(full.m_imp &~ m_imp)
  def ⊆ (s: FiniteSet[α]): Bool          = m_imp.subsetOf(s.m_imp)
  def contains(a: α)     : Bool          = m_imp.contains(ε.toℕ(a))
  def complement         : FiniteSet[α]  = ~this

  override def diff(s: GenSet[α]): FiniteSet[α] = s match
  {
    case s: FiniteSet[α] ⇒ this \ s
    case _               ⇒ super.diff(s)
  }

  override def union(s: GenSet[α]): FiniteSet[α] = s match
  {
    case s: FiniteSet[α] ⇒ this ∪ s
    case _               ⇒ super.union(s)
  }

  override def intersect(s: GenSet[α]): FiniteSet[α] = s match
  {
    case s: FiniteSet[α] ⇒ this ∩ s
    case _               ⇒ super.intersect(s)
  }

  override def subsetOf(s: GenSet[α]): Bool = s match
  {
    case s: FiniteSet[α] ⇒ this ⊆ s
    case _               ⇒ super.subsetOf(s)
  }

  override def equals(a: Any): Bool = a match
  {
    case s: FiniteSet[α] ⇒ this.m_imp equals s.m_imp
    case _               ⇒ super.equals(a)
  }

  def symdiff(s: GenSet[α]): FiniteSet[α] = s match
  {
    case s: FiniteSet[α] ⇒ this ⊖ s
    case _               ⇒ union(s) diff intersect(s)
  }

  override def size: ℕ                            = m_imp.size
  override def empty: FiniteSet[α]                = FiniteSet.empty
  override def hashCode: ℕ                        = m_imp.hashCode
  override def newBuilder: Builder[α,FiniteSet[α]]= FiniteSet.newBuilder
  override def toString: String                   = mkString("{",",","}")
}

/**
 * The companion object for class [[FiniteSet]].
 *
 * @author Jonathon Bell
 */
object FiniteSet
{
  def full[α](implicit ε: Finite[α]): FiniteSet[α] =
  {
    val n = ε.size >> 6               // size / 64
    val a = Array.fill(1 + n)(~0L)
    a(n) &= (1 << (ε.size & 63)) - 1  // size % 64
    fromBitMask(a)
  }

  def empty[α: Finite]                   : FiniteSet[α] = fromBitMask(Array())

  def apply[α: Finite](s: α*)            : FiniteSet[α] = (newBuilder ++= s).result

  def apply[α: Finite](s: Traversable[α]): FiniteSet[α] = (newBuilder ++= s).result

  def fromBitMask[α](mask: Array[Long])(implicit ε: Finite[α]): FiniteSet[α] =
  {
    val size = ε.size
    val words = size / 64
    new FiniteSet(BitSet.fromBitMaskNoCopy(mask))
  }

  class CanBuildFrom[α: Finite] extends CBF[Set[_],α,FiniteSet[α]]
  {
    def apply()         : Builder[α,FiniteSet[α]] = newBuilder
    def apply(s: Set[_]): Builder[α,FiniteSet[α]] = newBuilder
  }

  class isPartiallyOrdered[α: Finite] extends PartialOrdering[FiniteSet[α]]
  {
    def lteq(s: FiniteSet[α],t: FiniteSet[α]): Bool = s ⊆ t

    def tryCompare(s: FiniteSet[α],t: FiniteSet[α]) = (s ⊆ t,s ⊇ t) match
    {
      case (true, true)  ⇒ Some( 0)
      case (true, false) ⇒ Some(-1)
      case (false,true)  ⇒ Some(+1)
      case (fasle,false) ⇒ None
    }
  }

  private
  def newBuilder[α](implicit ε: Finite[α]) = new Builder[α,FiniteSet[α]]
  {
    val m_mask            = new Array[Long](1 + (ε.size >> 6))
    val m_bits            = mBitSet.fromBitMaskNoCopy(m_mask)

    def +=(n: α)          = {m_bits += ε.toℕ(n); this}
    def clear()           = java.util.Arrays.fill(m_mask,0L)
    def result()          = fromBitMask(m_mask)
  }

  private
  def fromBitSet[α](bits: BitSet)(implicit ε: Finite[α]): FiniteSet[α] =
  {
    new FiniteSet(bits)
  }

  private[core]
  class Factory[α: Finite]
  {
    val full                    : FiniteSet[α] = FiniteSet.full
    val empty                   : FiniteSet[α] = FiniteSet.empty
    def apply(s: α*)            : FiniteSet[α] = FiniteSet.apply(s:_*)
    def apply(s: Traversable[α]): FiniteSet[α] = FiniteSet.apply(s)
  }
}

//****************************************************************************
