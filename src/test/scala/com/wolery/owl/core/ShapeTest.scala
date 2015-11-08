//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for class Shape.
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

class ShapeTest extends FunSuite
{
  test("Shape is transposing")
  {
    implicit val α = Arbitrary(choose(-128,128))

    isTransposing[Shape]()
  }
}

//****************************************************************************
