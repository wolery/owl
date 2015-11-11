//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for class Pitch.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core;

//****************************************************************************

import org.scalacheck.Arbitrary
import org.scalatest.FunSuite

import CoreTest._
import CoreTest.arbitrary._

//****************************************************************************

class PitchTest extends FunSuite
{
  implicit val i = Arbitrary(generate.int)               // For i ∈ [-128,128]

  test("Pitch is intervallic")
  {
    isIntervallic[Pitch]()                               // Verify the axioms
  }

  test("Pitch is totally ordered")
  {
    isOrdered[Pitch]()                                   // Verify the axioms
  }

  test("Pitch ⇒ Note is equivarient")
  {
    isEquivariant[Pitch,Note,ℤ](_.note)                  // Verify the axioms
  }

  test("Pitch construction")
  {
    forAll("p") {(p: Pitch) ⇒
    {
      assert(p == Pitch(p.midi),         "[midi]")
      assert(p == Pitch(p.frequency),    "[frequency]")
      assert(p == Pitch(p.note,p.octave),"[note+octave]")
    }}
  }
}

//****************************************************************************
