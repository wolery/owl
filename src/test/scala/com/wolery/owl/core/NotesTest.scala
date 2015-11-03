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

  test("Notes(<=) is a partial ordering")
  {
    implicit object α extends PartialOrdering[Notes]
    {
      def lteq(s: Notes,t: Notes) = s ⊆ t
      def tryCompare(x: Notes,y: Notes) = ???
    }

    isPartiallyOrdered[Notes]()
  }
}

//****************************************************************************
