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
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package core

//****************************************************************************

class FrequencyTest extends CoreSuite
{
  import arbitrary._                                     // For Owl implicits

  test("Frequency is an ℝ-torsor")
  {
    isTorsor[Frequency,ℝ]()                              // Verify the axioms
  }

  test("Frequency is totally ordered")
  {
    isOrdered[Frequency]()                               // Verify the axioms
  }
}

//****************************************************************************
