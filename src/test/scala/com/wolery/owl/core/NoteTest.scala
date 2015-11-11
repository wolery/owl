//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for class Note.
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

class NoteTest extends FunSuite
{
  implicit val i = Arbitrary(generate.int)               // For i ∈ [-128,128]

  test("Note is intervallic")
  {
    isIntervallic[Note]()                                // Verify the axioms
  }

  test("Note construction")
  {
    forAll("n") {(n: Note) ⇒
    {
      for (o ← -1 to 9)
      {
        assert(n(o) == Pitch(n,o),"[octave]")
      }
    }}
  }
}

//****************************************************************************
