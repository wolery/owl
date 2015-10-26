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

import org.scalacheck.Arbitrary
import org.scalacheck.Gen.oneOf
import org.scalacheck.Gen.choose
import org.scalatest.FunSuite

import CoreTest._
import Notes._

//****************************************************************************

class NotesTest extends FunSuite
{
  implicit val α = Arbitrary(for (bits ← choose(0,0xFFF)) yield Notes(bits))
  implicit val β = Arbitrary(oneOf(Note.notes))
  implicit val γ = Arbitrary(choose(-128,128))

  test("Notes is transposing")
  {
    isTransposing[Notes]()
  }
}

//****************************************************************************
