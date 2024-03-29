// ----------------------------------------------------------------------------
// Copyright 2006, Martin D. Flynn
// All rights reserved
// ----------------------------------------------------------------------------
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
// http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// ----------------------------------------------------------------------------
// Change History:
//  2006/03/26  Martin D. Flynn
//      Initial release
//  2006/04/23  Martin D. Flynn
//      Integrated logging changes made to Print
// ----------------------------------------------------------------------------
package org.opendmtp.server_mysql;

import java.io.File;

import org.opendmtp.codes.ServerErrors;
import org.opendmtp.server.base.AccountID;
import org.opendmtp.server.base.DeviceID;
import org.opendmtp.server.base.Event;
import org.opendmtp.server.base.Packet;
import org.opendmtp.server.base.PacketParseException;
import org.opendmtp.util.FileTools;
import org.opendmtp.util.Print;
import org.opendmtp.util.RTConfig;

/**
 * Parses the event packets for a device and adds the event data to a table.
 * 
 * @author Martin D. Flynn
 * @author George Lee
 */
public class ParseFile {

  // ------------------------------------------------------------------------
  // This class will read event packets from a file and insert them into the
  // EventData table for the specified Device.

  /**
   * Parse the event data from the command line. Configures DBConfig using options from the command
   * line. Loads the account and device IDs and parses the event data from the packet file
   * specified.
   * <p>
   * 
   * System exits with an error if there are problems with loading the IDs or with reading the
   * packets.
   * 
   * @param argv The command line arguments to pass to DBConfig.
   */
  public static void main(String argv[]) {
    DBConfig.init(argv, true);
    // Arguments:
    // -acct=<AccountID>
    // -dev=<DeviceID>
    // -file=<PacketFile>

    /* load account/device */
    String acctID = "", devID = "";
    AccountID account = null;
    DeviceID device = null;
    try {
      // Account
      acctID = RTConfig.getString("acct", "");
      account = AccountID.loadAccountID(acctID);
      // Device
      devID = RTConfig.getString("dev", "");
      device = DeviceID.loadDeviceID(account, devID);
    }
    catch (PacketParseException ppe) {
      Print.logException("Unable to load DeviceID: " + acctID + "/" + devID, ppe);
      System.exit(1);
    }

    /* read file */
    File evFile = RTConfig.getFile("file", null);
    byte pktData[] = FileTools.readFile(evFile);
    if (pktData == null) {
      Print.logError("Unable to read packet file: " + evFile);
      System.exit(1);
    }

    /* parse data */
    int pktOfs = 0;
    for (; pktOfs < pktData.length;) {
      int len = Packet.getPacketLength(pktData, pktOfs);
      if (len < 0) {
        Print.logError("Found invalid packet at offset " + pktOfs);
        break;
      }
      byte pkt[] = new byte[len];
      System.arraycopy(pktData, pktOfs, pkt, 0, len);
      try {
        Packet packet = new Packet(device, true/* isClient */, pkt); // client packet
        if (packet.isEventType()) {
          Event evData = new Event(packet);
          int err = device.saveEvent(evData);
          if (err != ServerErrors.NAK_OK) {
            Print.logError("Event insertion error: " + err);
          }
          else {
            Print.logDebug("Saved event: " + evData);
          }
        }
      }
      catch (PacketParseException ppe) {
        Print.logException("Unable to parse packet", ppe);
        // System.exit(1);
      }
      pktOfs += len;
    }

  }

}
