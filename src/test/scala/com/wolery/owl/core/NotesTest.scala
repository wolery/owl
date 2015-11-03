//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for class Notes.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core;

//****************************************************************************

import org.scalacheck.Arbitrary
import org.scalacheck.Gen.choose
import org.scalatest.FunSuite

import CoreTest._
import CoreTest.arbitrary._

//****************************************************************************

class NotesTest extends FunSuite
{
  test("Notes is transposing")
  {
    implicit val α = Arbitrary(choose(-128,128))

    isTransposing[Notes]()
  }

  test("Notes(∪) is an abelian monoid")
  {
    implicit object α extends Monoid[Notes]
    {
      def zero                    = Notes()
      def plus(a: Notes,b: Notes) = a union b
    }

    isMonoid[Notes]()
    isCommutative[Notes]()
  }

  test("Notes(∩) is an abelian monoid")
  {
    implicit object α extends Monoid[Notes]
    {
      def zero                    = Notes(0xFFF)
      def plus(a: Notes,b: Notes) = a intersect b
    }

    isMonoid[Notes]()
    isCommutative[Notes]()
  }

  test("Notes(⊆) is a partial ordering")
  {
    implicit object α extends PartialOrdering[Notes]
    {
      def lteq(s: Notes,t: Notes) = s ⊆ t
      def tryCompare(x: Notes,y: Notes) = ???
    }

    isPartiallyOrdered[Notes]()
  }

  test("C major looks ok")
  {
    import scala.language.postfixOps

    val Cmaj = Notes(C,D,E,F,G,A,B)
    val Gmaj = Notes(G,A,B,C,D,E,F♯)

    assert(Cmaj.size == 7,                "[size]")

    assert(C   ∈ Cmaj,                    "[C ]")
    assert(C.♯ ∉ Cmaj,                    "[C♯]")
    assert(D   ∈ Cmaj,                    "[D ]")
    assert(D.♯ ∉ Cmaj,                    "[D♯]")
    assert(E   ∈ Cmaj,                    "[E ]")
    assert(F   ∈ Cmaj,                    "[F ]")
    assert(F.♯ ∉ Cmaj,                    "[F♯]")
    assert(G   ∈ Cmaj,                    "[G ]")
    assert(G.♯ ∉ Cmaj,                    "[G♯]")
    assert(A   ∈ Cmaj,                    "[A ]")
    assert(A.♯ ∉ Cmaj,                    "[A♯]")
    assert(B   ∈ Cmaj,                    "[B ]")

    assert(Cmaj+7 == Gmaj,                "[Gmaj]")

    assert(Cmaj \ Gmaj == Notes(F),       "[\\]")
    assert(Cmaj ∪ Gmaj == Cmaj + F.♯,     "[∪]")
    assert(Cmaj ∩ Gmaj == Cmaj - F,       "[∩]")
    assert(Cmaj ⊖ Gmaj == Notes(F,F♯),    "[⊖]")
    assert(~Cmaj == Notes(C♯,D♯,F♯,G♯,A♯),"[~]")
  }
}

//****************************************************************************
