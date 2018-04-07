//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose :
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package owl
package interpreter

import java.io.Writer

//****************************************************************************

abstract sealed class          Result
case object Success    extends Result
case object Error      extends Result
case object Incomplete extends Result

trait Interpreter
{
  def writer                                        : Writer
  def writer_=  (writer: Option[Writer])            : Unit
  def interpret (line: String)                      : Result
  def bind[α]   (name: String,tipe: String,value: α): Result
}

//****************************************************************************
