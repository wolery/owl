//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for class Scale.
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

class ScaleTest extends FunSuite
{
  test("Scale is transposing")
  {
    implicit val α = Arbitrary(choose(-128,128))

    isTransposing[Scale]()
  }
}

//****************************************************************************
