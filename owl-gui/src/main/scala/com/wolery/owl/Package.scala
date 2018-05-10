//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Common definitions used throughout the application.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery

//****************************************************************************

/**
 * Common definitions used throughout the application.
 *
 * @author Jonathon Bell
 */
package object owl
{
  type Logging     = com.wolery.util.Logging
  type Preferences = com.wolery.util.Preferences
  type Application = com.wolery.fx.util.Application

  type fx          = javafx.fxml.FXML
  type Node        = javafx.scene.Node
  type Pane        = javafx.scene.layout.Pane
}

//****************************************************************************
