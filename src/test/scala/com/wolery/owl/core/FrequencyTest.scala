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

//****************************************************************************

import org.scalatest.FunSuite

import CoreTest._
import CoreTest.arbitrary._

//****************************************************************************

class FrequencyTest extends FunSuite
{
  test("Frequency is an ℝ-Torsor")
  {
    isTorsor[Frequency,ℝ]()
  }

  test("Frequency is totally ordered")
  {
    isOrdered[Frequency]()
  }
}

//****************************************************************************
