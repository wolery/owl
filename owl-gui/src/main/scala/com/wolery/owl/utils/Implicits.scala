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
//*
//*
//****************************************************************************

package com.wolery.owl.utils

//****************************************************************************

import scala.language.implicitConversions

import javafx.concurrent.Task
import javafx.event.Event
import javafx.event.EventHandler
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

//****************************************************************************

object implicits
{
  implicit
  def asEventHandler[E <: Event](lambda: E ⇒ Unit): EventHandler[E] = new EventHandler[E]
  {
    def handle(e: E) = lambda(e)
  }

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
