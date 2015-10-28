//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for class Frequency.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core;

import org.scalacheck.Arbitrary
import org.scalacheck.Gen.choose
import org.scalatest.FunSuite

import CoreTest._

//****************************************************************************

class FrequencyTest extends FunSuite
{
  implicit val α = Arbitrary(for (r ← choose(2.0,10.0)) yield Hz(Math.exp(r)))
  implicit val β = Arbitrary(choose(-128.0,128.0))

  test("Frequency is an ℝ-Torsor")
  {
    isTorsor[Frequency,ℝ]()
  }

  test("Frequency is totally ordered")
  {
    isTotallyOrdered[Frequency]()
  }
}

//****************************************************************************
