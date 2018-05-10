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

import javafx.scene.control._
import javafx.scene.layout._
import com.wolery.fx.control.menu._
import com.wolery.owl.temporal._

//****************************************************************************

class BeatsProjectLargeView extends Display
{
  this.getChildren.addAll(

    new VBox(
    new Counter("transport-display-bar",
                "Playhead Position (Bars)"),
    new Label("BAR")),

    new VBox(
    new Counter("transport-display-beat",
                "Playhead Position (Bars)"),
    new Label("BEAT")),

    new VBox(
    new Counter("transport-display-div",
                "Playhead Position (Bars)"),
    new Label("DIV")),

    new VBox(
    new Counter("transport-display-tick",
                "Playhead Position (Bars)"),
    new Label("TICK")),

    new Separator(),

    new VBox(
      new Counter("transport-display-tempo",
                  "Tempo"),
      new DropDown("transport-display-tempo-mode",
                   "Project Tempo menu",
        new MenuItem("KEEP - Keep Project Tempo"),
        new MenuItem("ADAPT - Adapt Project Tempo"),
        new MenuItem("AUTO - Automatic Mode"),
        new SeparatorMenuItem(),
        new MenuItem("Smart Tempo Project Settings...")),
      new Label("Tempo")),

    new VBox(
      new DropDown("transport-display-time",
                   "Time Signature",
        new RadioMenuItem("3/4"),
        new RadioMenuItem("4/4"),
        new RadioMenuItem("5/4"),
        new RadioMenuItem("6/8"),
        new RadioMenuItem("7/8"),
        new RadioMenuItem("12/8"),
        new SeparatorMenuItem(),
        new RadioMenuItem("3/2"),
        new RadioMenuItem("13/2"),
        new RadioMenuItem("14/2"),
        new RadioMenuItem("15/2"),
        new RadioMenuItem("16/2"),
        new RadioMenuItem("17/2"),
        new RadioMenuItem("18/2"),
        new RadioMenuItem("19/2"),
        new RadioMenuItem("20/2"),
        new RadioMenuItem("21/2"),
        new SeparatorMenuItem(),
        new MenuItem("Custom...")),
      new Label("Time")),

    new VBox(
      new DropDown("transport-display-key",
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
        new RadioMenuItem("C♭ minor")),
      new Label("Key")),

    new DropDown("transport-display-mode",
                 "Display Mode",
      new MenuItem("Beats &amp; Project"),
      new MenuItem("Beats &amp; Project (Large)"),
      new MenuItem("Beats &amp; Time"),
      new MenuItem("Beats &amp; Time (Large)"),
      new MenuItem("Beats"),
      new MenuItem("Time"),
      new MenuItem("Custom"),
      new SeparatorMenuItem(),
      new MenuItem("Open Giant Beats Display"),
      new MenuItem("Open Giant Time Display"),
      new SeparatorMenuItem(),
      new MenuItem("Use SMPTE View offset"),
      new SeparatorMenuItem(),
      new MenuItem("Customize Control Bar And Display..."),
      new MenuItem("Apply Defaults"),
      new MenuItem("Save As Defaults"))
  )
}

//****************************************************************************
