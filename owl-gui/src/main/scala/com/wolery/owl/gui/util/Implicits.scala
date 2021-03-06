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
package gui
package util

//****************************************************************************

import scala.language.implicitConversions

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.concurrent.Task

//****************************************************************************

object implicits
{
  implicit
  def asTask[R](lambda: ⇒ R): Task[R] = new Task[R]
  {
    def call(): R = lambda
  }

  implicit
  def asRunnable(lambda: ⇒ Unit): Runnable =  new Runnable
  {
    def run(): Unit = lambda
  }

  implicit
  def asChangeListener[α](lambda: (α,α) ⇒ Unit) = new ChangeListener[α]
  {
    def changed(o: ObservableValue[_ <: α],w: α,n: α): Unit = lambda(w,n)
  }

  implicit
  def asChangeListener[α](lambda: (ObservableValue[_ <: α],α,α) ⇒ Unit) = new ChangeListener[α]
  {
    def changed(o: ObservableValue[_ <: α],w: α,n: α): Unit = lambda(o,w,n)
  }
}

//****************************************************************************
