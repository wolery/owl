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

class PitchTest extends CoreSuite
{
  import arbitrary._                                     // For owl implicits

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
      assert(p == Pitch(p.midi),                         "[Midi]")
      assert(p == Pitch(p.frequency),                    "[Frequency")
      assert(p == Pitch(p.note,p.octave),                "[Note,Octave")
    }}
  }

  test("Pitch reference")
  {
    assert(A(4).midi      == 69,                         "[A4]")
    assert(A(4).frequency == Hz(440.0),                  "[A440]")
  }
}

//****************************************************************************
