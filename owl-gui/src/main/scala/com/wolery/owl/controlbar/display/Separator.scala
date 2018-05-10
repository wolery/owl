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
package controlbar
package display

import javafx.geometry.Orientation.VERTICAL
import javafx.scene.control._
import javafx.scene.layout._
import javafx.scene.Group

//****************************************************************************

class Separator extends javafx.scene.control.Separator(VERTICAL)
{
// setId("")
// getStyleClass.setAll("transport-display-separator")
}

class Label(text: String) extends javafx.scene.control.Label(text)
{
//setId("")
  getStyleClass.setAll("transport-display-segment-label")
}

class DropDown(id: String,tip: String,item: MenuItem*) extends MenuButton
{
  setId(id)
  setText("KEEP")
  getStyleClass.setAll("transport-display-drop-down")
  setTooltip(new Tooltip(tip))
  getItems.addAll(item:_*)
}

class Counter(id: String,tip: String) extends TextField
{
//m_digits:  ℕ
//m_maximum: ℕ
//m_minimum: ℕ
//id
//style
//tooltip

  setId(id)
  getStyleClass.setAll("transport-display-counter")
  setTooltip(new Tooltip(tip))
  setText("001")
}

class CounterGroup(counters: Counter*) extends HBox
{}

class KeySignature extends DropDown(
        "transport-display-key",
        "Key Signature",
        new RadioMenuItem("B major" ),
        new RadioMenuItem("B♭ major"),
        new RadioMenuItem("A major" ),
        new RadioMenuItem("A♭ major"),
        new RadioMenuItem("G major" ),
        new RadioMenuItem("G♭ major"),
        new RadioMenuItem("F♯ major"),
        new RadioMenuItem("F major" ),
        new RadioMenuItem("E major" ),
        new RadioMenuItem("E♭ major"),
        new RadioMenuItem("D major" ),
        new RadioMenuItem("D♭ major"),
        new RadioMenuItem("C♯ major"),
        new RadioMenuItem("C major" ),
        new RadioMenuItem("C♭ major"),
        new SeparatorMenuItem(),
        new RadioMenuItem("B minor" ),
        new RadioMenuItem("B♭ minor"),
        new RadioMenuItem("A minor" ),
        new RadioMenuItem("A♭ minor"),
        new RadioMenuItem("G minor" ),
        new RadioMenuItem("G♭ minor"),
        new RadioMenuItem("F♯ minor"),
        new RadioMenuItem("F minor" ),
        new RadioMenuItem("E minor" ),
        new RadioMenuItem("E♭ minor"),
        new RadioMenuItem("D minor" ),
        new RadioMenuItem("D♭ minor"),
        new RadioMenuItem("C♯ minor"),
        new RadioMenuItem("C minor" ),
        new RadioMenuItem("C♭ minor"))

object create
{
  def displayMode()               = ???
  def segmentSeparator= ???

  def keySignature()  = ???
  def timeSignature() = ???

  def barBeat() = ???
  def barBeatDivTick() = ???
  def bb() = ???
  def bbdt() = ???

  def bbbb_b_d_t() = ???

  def hourMinuteSecond()           = ???
  def hourMinSecFrameSub()         = ???

  def hms()                        = ???
  def hmsfs()                      = ???

  def hh_mm_ss()       = ???
  def hh_mm_ss_ff_ss() = ???
//**

// PH  LL RR PI PO
//
// bar
// beat
// div
// tick

// hour
// min
// sec
// frame
// sub

// tempo bpm
// tempo pcnt

// time signature
// time signature - division

// key  signature
// project end
}

//****************************************************************************
