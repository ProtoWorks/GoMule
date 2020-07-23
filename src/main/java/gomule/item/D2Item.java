/*******************************************************************************
 *
 * Copyright 2007 Andy Theuninck, Randall & Silospen
 *
 * This file is part of gomule.
 *
 * gomule is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * gomule is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * gomlue; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 *
 ******************************************************************************/

package gomule.item;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.awt.Color;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ComparisonChain;

import gomule.util.D2BitReader;
import gomule.util.D2ItemException;
import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFile;
import randall.d2files.D2TxtFileItemProperties;
import randall.flavie.D2ItemInterface;

/*
an item class
manages one item
keeps the a copy of the bytes representng
an item and a bitreader to manipulate them
also stores most data from the item in
this class is NOT designed to edit items
any methods that allow the item's bytes
to be written only exist to facillitate
moving items. writing other item fields
inis not supported by this class
 */
@Getter
public class D2Item implements Comparable<D2Item>, D2ItemInterface {
    
    private D2PropCollection propCollection = new D2PropCollection();
    
    private List<Object> socketedItems;
    
    private int flags;
    
    private short version;
    
    private short location;
    
    private short bodyPosition;
    
    private short row;
    
    private short col;
    
    private short panel;
    
    private String item_type;
    
    private short socketNrFilled = 0;
    
    private short socketNrTotal = 0;
    
    private long fingerprint;
    
    private short ilvl;
    
    private short quality;
    
    private short gfx_num;
    
    private D2TxtFileItemProperties automod_info;
    
    private short[] rare_prefixes;
    
    private short[] rare_suffixes;
    
    private String personalization;
    
    private short width;
    
    private short height;
    
    private String imageFile;
    
    private D2TxtFileItemProperties itemType;
    
    private String type;
    
    private String type2;
    
    private boolean ethereal;
    
    private boolean socketed;
    
    private boolean isThrow;
    
    private boolean magical;
    
    private boolean rare;
    
    private boolean crafted;
    
    private boolean set;
    
    private boolean unique;
    
    private boolean runeWord;
    
    private boolean smallCharm;
    
    private boolean largeCharm;
    
    private boolean grandCharm;
    
    private boolean jewel;
    
    //	private boolean equipped = false;
    
    private boolean gem;
    
    private boolean stackable = false;
    
    private boolean rune;
    
    private boolean typeMisc;
    
    private boolean identified;
    
    private boolean typeWeapon;
    
    private boolean typeArmor;
    
    protected String itemName;
    
    protected String baseItemName;
    
    private short curDur;
    
    private short maxDur;
    
    private short def;
    
    private short cBlock;
    
    private short block;
    
    private short initDef;
    
    private short[] i1Dmg;
    private short[] i2Dmg;
    
    // 0 FOR BOTH 1 FOR 1H 2 FOR 2H
    private int whichHand;
    
    // private int lvl;
    private String fP;
    
    private String gUID;
    
    private boolean body = false;
    
    private String bodyLoc1;
    
    private String bodyLoc2;
    
    private boolean belt = false;
    
    private D2BitReader item;
    
    private String fileName;
    
    private boolean isCharacter;
    
    @Setter
    private int charLvl;
    
    private int reqLvl = -1;
    
    private int reqStr = -1;
    
    private int reqDex = -1;
    
    private String setName;
    
    private int setSize;
    
    private String itemQuality = "none";
    
    private short setId;
    
    @SneakyThrows
    public D2Item(String fileName, D2BitReader file, int pos, long charLvl) {
        this.fileName = fileName;
        isCharacter = this.fileName.endsWith(".d2s");
        this.charLvl = (int) charLvl;
        
        try {
            file.set_byte_pos(pos);
            readItem(file, pos);
            int lCurrentReadLength = file.get_pos() - pos * 8;
            int lNextJMPos = file.findNextFlag("JM", file.get_byte_pos());
            int lLengthToNextJM = lNextJMPos - pos;
            
            if (lLengthToNextJM < 0) {
                int lNextKFPos = file.findNextFlag("kf", file.get_byte_pos());
                int lNextJFPos = file.findNextFlag("jf", file.get_byte_pos());
                if (lNextJFPos >= 0) {
                    
                    lLengthToNextJM = lNextJFPos - pos;
                    
                } else if (lNextKFPos >= 0) {
                    lLengthToNextJM = lNextKFPos - pos;
                } else {
                    // last item (for stash only)
                    lLengthToNextJM = file.get_length() - pos;
                }
            } else if ((lNextJMPos > file.findNextFlag("kf", file
                    .get_byte_pos()))
                    && (pos < file.findNextFlag("kf", file.get_byte_pos()))) {
                lLengthToNextJM = file
                        .findNextFlag("kf", file.get_byte_pos())
                        - pos;
            } else if ((lNextJMPos > file.findNextFlag("jf", file
                    .get_byte_pos()))
                    && (pos < file.findNextFlag("jf", file.get_byte_pos()))) {
                
                lLengthToNextJM = file
                        .findNextFlag("jf", file.get_byte_pos())
                        - pos;
                
            }
            
            int lDiff = ((lLengthToNextJM * 8) - lCurrentReadLength);
            if (lDiff > 7) {
                throw new D2ItemException(
                        "Item not read complete, missing bits: " + lDiff
                                + getExStr());
            }
            
            file.set_byte_pos(pos);
            item = new D2BitReader(file.get_bytes(lLengthToNextJM));
            file.set_byte_pos(pos + lLengthToNextJM);
        } catch (D2ItemException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new D2ItemException("Error: " + ex.getMessage() + getExStr());
        }
    }
    
    // read basic information from the bytes
    // common to all items, then split based on
    // whether the item is an ear
    private void readItem(D2BitReader file, int pos) throws Exception {
        file.skipBytes(2);
        flags = (int) file.unflip(file.read(32), 32); // 4 bytes
        
        socketed = checkFlag(12);
        ethereal = checkFlag(23);
        runeWord = checkFlag(27);
        identified = checkFlag(5);
        version = (short) file.read(8);
        
        file.skipBits(2);
        location = (short) file.read(3);
        
        bodyPosition = (short) file.read(4);
        col = (short) file.read(4);
        row = (short) file.read(4);
        panel = (short) file.read(3);
        
        // flag 17 is an ear
        if (!checkFlag(17)) {
            
            readExtend(file);
        } else {
            readEar(file);
        }
        
        //Need to tidy up the properties before the item mods are calculated.
        propCollection.deleteUselessProperties();
        
        if (isTypeArmor() || isTypeWeapon()) {
            //			Blunt does 150 damage to undead
            if (type.equals("club") || type.equals("scep")
                    || type.equals("mace") || type.equals("hamm")) {
                propCollection.add(new D2Prop(122, new int[]{150}, 0));
            }
            applyItemMods();
        }
    }
    
    // read ear related data from the bytes
    private void readEar(D2BitReader file) {
        
        int eClass = (int) file.read(3);
        int eLevel = (int) (file.read(7));
        
        StringBuffer lCharName = new StringBuffer();
        for (int i = 0; i < 18; i++)
        // while(1==1)
        {
            long lChar = file.read(7);
            if (lChar != 0) {
                lCharName.append((char) lChar);
            } else {
                file.read(7);
                break;
            }
        }
        itemType = D2TxtFile.search("ear");
        height = Short.parseShort(itemType.get("invheight"));
        width = Short.parseShort(itemType.get("invwidth"));
        imageFile = itemType.get("invfile");
        baseItemName = itemName = lCharName.toString() + "'s Ear";
        
        propCollection.add(new D2Prop(185, new int[]{eClass, eLevel}, 0, true, 39));
        
        // for (int i = 0; i < 18; i++) {
        // file.read(7); // name
        // }
    }
    
    // read non ear data from the bytes,
    // setting class variables for easier access
    private void readExtend(D2BitReader file) throws Exception {
        // 9,5 bytes already read (common data)
        item_type = "";
        // skip spaces or hashing won't work
        for (int i = 0; i < 4; i++) {
            char c = (char) file.read(8); // 4 bytes
            if (c != 32) {
                item_type += c;
            }
        }
        
        itemType = D2TxtFile.search(item_type);
        height = Short.parseShort(itemType.get("invheight"));
        width = Short.parseShort(itemType.get("invwidth"));
        imageFile = itemType.get("invfile");
        
        String lD2TxtFileName = itemType.getFileName();
        if (lD2TxtFileName != null) {
            typeMisc = ("Misc".equals(lD2TxtFileName));
            typeWeapon = ("weapons".equals(lD2TxtFileName));
            typeArmor = ("armor".equals(lD2TxtFileName));
        }
        
        type = itemType.get("type");
        type2 = itemType.get("type2");
        
        // Shields - block chance.
        if (isShield()) {
            cBlock = Short.parseShort(itemType.get("block"));
        }
        
        // Requerements
        if (typeMisc) {
            reqLvl = getReq(itemType.get("levelreq"));
        } else if (typeArmor) {
            reqLvl = getReq(itemType.get("levelreq"));
            reqStr = getReq(itemType.get("reqstr"));
            
            D2TxtFileItemProperties qualSearch = D2TxtFile.ARMOR.searchColumns(
                    "normcode", item_type);
            itemQuality = "normal";
            if (qualSearch == null) {
                qualSearch = D2TxtFile.ARMOR.searchColumns("ubercode",
                                                           item_type);
                itemQuality = "exceptional";
                if (qualSearch == null) {
                    qualSearch = D2TxtFile.ARMOR.searchColumns("ultracode",
                                                               item_type);
                    itemQuality = "elite";
                }
            }
            
        } else if (typeWeapon) {
            reqLvl = getReq(itemType.get("levelreq"));
            reqStr = getReq(itemType.get("reqstr"));
            reqDex = getReq(itemType.get("reqdex"));
            
            D2TxtFileItemProperties qualSearch = D2TxtFile.WEAPONS
                    .searchColumns("normcode", item_type);
            itemQuality = "normal";
            if (qualSearch == null) {
                qualSearch = D2TxtFile.WEAPONS.searchColumns("ubercode",
                                                             item_type);
                itemQuality = "exceptional";
                if (qualSearch == null) {
                    qualSearch = D2TxtFile.WEAPONS.searchColumns("ultracode",
                                                                 item_type);
                    itemQuality = "elite";
                }
            }
        }
        
        String lItemName = D2TblFile.getString(item_type);
        if (lItemName != null) {
            itemName = lItemName;
            baseItemName = itemName;
        }
        
        // flag 22 is a simple item (extend1)
        if (!checkFlag(22)) {
            readExtend1(file);
        }
        
        // gold (?)
        if ("gold".equals(item_type)) {
            if (file.read(1) == 0) {
                file.read(12);
            } else {
                file.read(32);
            }
        }
        
        long lHasGUID = file.read(1);
        
        if (lHasGUID == 1) { // GUID ???
            if (type.startsWith("rune") || type.startsWith("gem")
                    || type.startsWith("amu") || type.startsWith("rin")
                    || isCharm() || !isTypeMisc()) {
                
                gUID = "0x" + Integer.toHexString((int) file.read(32))
                        + " 0x" + Integer.toHexString((int) file.read(32))
                        + " 0x" + Integer.toHexString((int) file.read(32));
            } else {
                file.read(3);
            }
        }
        
        // flag 22 is a simple item (extend2)
        if (!checkFlag(22)) {
            readExtend2(file);
        }
        
        if (type != null && type2 != null && type.startsWith("gem")) {
            if (type2.equals("gem0") || type2.equals("gem1")
                    || type2.equals("gem2") || type2.equals("gem3")
                    || type2.equals("gem4")) {
                readPropertiesGems(file);
                gem = true;
            }
        }
        
        if (type != null && type2 != null && type.startsWith("rune")) {
            readPropertiesGems(file);
            rune = true;
        }
        
        D2TxtFileItemProperties lItemType = D2TxtFile.ITEM_TYPES.searchColumns(
                "Code", type);
        
        if (lItemType == null) {
            lItemType = D2TxtFile.ITEM_TYPES.searchColumns("Equiv1", type);
            if (lItemType == null) {
                lItemType = D2TxtFile.ITEM_TYPES.searchColumns("Equiv2", type);
            }
        }
        
        if ("1".equals(lItemType.get("Body"))) {
            body = true;
            bodyLoc1 = lItemType.get("BodyLoc1");
            bodyLoc2 = lItemType.get("BodyLoc2");
        }
        if ("1".equals(lItemType.get("Beltable"))) {
            belt = true;
            readPropertiesPots(file);
        }
        
        int lLastItem = file.get_byte_pos();
        
        if (socketNrFilled > 0) {
            socketedItems = new ArrayList<>();
            
            for (int i = 0; i < socketNrFilled; i++) {
                int lStartNewItem = file.findNextFlag("JM", lLastItem);
                D2Item lSocket = new D2Item(fileName, file, lStartNewItem,
                                            charLvl);
                lLastItem = lStartNewItem + lSocket.getItemLength();
                socketedItems.add(lSocket);
                
                if (lSocket.isJewel()) {
                    propCollection.addAll(lSocket.getPropCollection(), 1);
                } else if (isTypeWeapon()) {
                    propCollection.addAll(lSocket.getPropCollection(), 7);
                } else if (isTypeArmor()) {
                    if (type.equals("tors") || type.equals("helm")
                            || type.equals("phlm") || type.equals("pelt")
                            || type.equals("cloa") || type.equals("circ")) {
                        propCollection.addAll(lSocket.getPropCollection(), 8);
                    } else {
                        propCollection.addAll(lSocket.getPropCollection(), 9);
                    }
                }
                if (lSocket.reqLvl > reqLvl) {
                    reqLvl = lSocket.reqLvl;
                }
                
            }
        }
        
        if (runeWord) {
            List<Object> lList = new ArrayList<>();
            for (int i = 0; i < socketedItems.size(); i++) {
                lList.add(((D2Item) socketedItems.get(i)).getRuneCode());
            }
            
            D2TxtFileItemProperties lRuneWord = D2TxtFile.RUNES
                    .searchRuneWord(lList);
            if (lRuneWord != null) {
                itemName = D2TblFile.getString(lRuneWord.get("Name"));
            }
        }
        
        if (socketNrFilled > 0 && isNormal()) {
            itemName = "Gemmed " + itemName;
        }
        
        if (itemName != null) {
            itemName = itemName.trim();
            
        }
        
        if (baseItemName != null) {
            baseItemName = baseItemName.trim();
            
        }
        
        if (ethereal) {
            if (reqStr != -1) {
                reqStr -= 10;
            }
            if (reqDex != -1) {
                reqDex -= 10;
            }
        }
    }
    
    private void readExtend1(D2BitReader file) throws Exception {
        // extended item
        socketNrFilled = (short) file.read(3);
        fingerprint = file.read(32);
        fP = "0x" + Integer.toHexString((int) fingerprint);
        ilvl = (short) file.read(7);
        quality = (short) file.read(4);
        propCollection = new D2PropCollection();
        // check variable graphic flag
        gfx_num = -1;
        if (file.read(1) == 1) {
            gfx_num = (short) file.read(3);
            if (itemType.get("namestr").compareTo("cm1") == 0) {
                smallCharm = true;
                imageFile = "invch" + ((gfx_num) * 3 + 1);
            } else if (itemType.get("namestr").compareTo("cm2") == 0) {
                largeCharm = true;
                imageFile = "invch" + ((gfx_num) * 3 + 2);
            } else if (itemType.get("namestr").compareTo("cm3") == 0) {
                grandCharm = true;
                imageFile = "invch" + ((gfx_num) * 3 + 3);
            } else if (itemType.get("namestr").compareTo("jew") == 0) {
                jewel = true;
                imageFile = "invjw" + (gfx_num + 1);
            } else {
                imageFile += (gfx_num + 1);
            }
        }
        // check class info flag
        if (file.read(1) == 1) {
            automod_info = D2TxtFile.AUTOMAGIC.getRow((int) file.read(11) - 1);
        }
        
        // path determined by item quality
        switch (quality) {
            case 1: // low quality item
            {
                short low_quality = (short) file.read(3);
                
                switch (low_quality) {
                    
                    case 0: {
                        itemName = "Crude " + itemName;
                        break;
                    }
                    
                    case 1: {
                        itemName = "Cracked " + itemName;
                        break;
                    }
                    
                    case 2: {
                        itemName = "Damaged " + itemName;
                        break;
                    }
                    
                    case 3: {
                        itemName = "Low Quality " + itemName;
                        break;
                    }
                    
                }
                
                break;
            }
            case 3: // high quality item
            {
                itemName = "Superior " + itemName;
                baseItemName = itemName;
                // 3bytes, don't know what they are.
                file.read(3);
                break;
            }
            case 4: // magic item
            {
                magical = true;
                short magic_prefix = (short) file.read(11);
                short magic_suffix = (short) file.read(11);
                
                if (magic_suffix == 0) {
                    magic_suffix = 10000;
                }
                
                D2TxtFileItemProperties lPrefix = D2TxtFile.PREFIX
                        .getRow(magic_prefix);
                String lPreName = lPrefix.get("Name");
                if (lPreName != null && !lPreName.equals("")) {
                    itemName = D2TblFile.getString(lPreName) + " " + itemName;
                    int lPreReq = getReq(lPrefix.get("levelreq"));
                    if (lPreReq > reqLvl) {
                        reqLvl = lPreReq;
                    }
                }
                
                D2TxtFileItemProperties lSuffix = D2TxtFile.SUFFIX
                        .getRow(magic_suffix);
                String lSufName = lSuffix.get("Name");
                if (lSufName != null && !lSufName.equals("")) {
                    itemName = itemName + " " + D2TblFile.getString(lSufName);
                    int lSufReq = getReq(lSuffix.get("levelreq"));
                    if (lSufReq > reqLvl) {
                        reqLvl = lSufReq;
                    }
                }
                applyAutomodLvl();
                break;
            }
            case 5: // set item
            {
                set = true;
                setId = (short) file.read(12);
                if (gfx_num == -1) {
                    String s = itemType.get("setinvfile");
                    if (s.compareTo("") != 0) { imageFile = s; }
                }
                
                D2TxtFileItemProperties lSet = D2TxtFile.SETITEMS.getRow(setId);
                itemName = D2TblFile.getString(lSet.get("index"));
                setName = lSet.get("set");
                
                setSize = (D2TxtFile.SETITEMS.searchColumnsMultipleHits("set",
                                                                        setName)).size();
                
                int lSetReq = getReq(lSet.get("lvl req"));
                if (lSetReq != -1 && lSetReq > reqLvl) {
                    reqLvl = lSetReq;
                }
                
                applyAutomodLvl();
                addSetProperties(D2TxtFile.FULLSET.searchColumns("index", D2TxtFile.SETITEMS.getRow(setId).get("set")));
                break;
            }
            case 7: {
                unique = true;
                short unique_id = (short) file.read(12);
                String s = itemType.get("uniqueinvfile");
                if (s.compareTo("") != 0) {
                    imageFile = s;
                }
                
                D2TxtFileItemProperties lUnique = D2TxtFile.UNIQUES
                        .getRow(unique_id);
                String lNewName = D2TblFile.getString(lUnique.get("index"));
                if (lNewName != null) {
                    itemName = lNewName;
                }
                
                if (lUnique.get("code").equals(item_type)) {
                    int lUniqueReq = getReq(lUnique.get("lvl req"));
                    if (lUniqueReq != -1) {
                        reqLvl = lUniqueReq;
                    }
                }
                applyAutomodLvl();
                break;
            }
            case 6: // rare item
            {
                rare = true;
                itemName = "Rare " + itemName;
            }
            case 8: // also a rare item, do the same (one's probably crafted)
            {
                if (!rare) {
                    crafted = true;
                    itemName = "Crafted " + itemName;
                }
            }
            
            applyAutomodLvl();
            short rare_name_1 = (short) file.read(8);
            short rare_name_2 = (short) file.read(8);
            D2TxtFileItemProperties lRareName1 = D2TxtFile.RAREPREFIX
                    .getRow(rare_name_1 - 156);
            D2TxtFileItemProperties lRareName2 = D2TxtFile.RARESUFFIX
                    .getRow(rare_name_2 - 1);
            itemName = D2TblFile.getString(lRareName1.get("name")) + " "
                    + D2TblFile.getString(lRareName2.get("name"));
            
            rare_prefixes = new short[3];
            rare_suffixes = new short[3];
            short pre_count = 0;
            short suf_count = 0;
            for (int i = 0; i < 3; i++) {
                if (file.read(1) == 1) {
                    rare_prefixes[pre_count] = (short) file.read(11);
                    D2TxtFileItemProperties lPrefix = D2TxtFile.PREFIX
                            .getRow(rare_prefixes[pre_count]);
                    pre_count++;
                    String lPreName = lPrefix.get("Name");
                    if (lPreName != null && !lPreName.equals("")) {
                        int lPreReq = getReq(lPrefix.get("levelreq"));
                        if (lPreReq > reqLvl) {
                            reqLvl = lPreReq;
                        }
                    }
                    
                }
                if (file.read(1) == 1) {
                    rare_suffixes[suf_count] = (short) file.read(11);
                    D2TxtFileItemProperties lSuffix = D2TxtFile.SUFFIX
                            .getRow(rare_suffixes[suf_count]);
                    suf_count++;
                    String lSufName = lSuffix.get("Name");
                    if (lSufName != null && !lSufName.equals("")) {
                        int lSufReq = getReq(lSuffix.get("levelreq"));
                        if (lSufReq > reqLvl) {
                            reqLvl = lSufReq;
                        }
                    }
                }
            }
            
            if (isCrafted()) {
                reqLvl = reqLvl + 10 + (3 * (suf_count + pre_count));
            }
            break;
            
            case 2: {
                readTypes(file);
                break;
            }
        }
        
        // rune word
        if (checkFlag(27)) {
            file.skipBits(12);
            file.skipBits(4);
        }
        // personalized
        if (checkFlag(25)) {
            personalization = "";
            boolean lNotEnded = true;
            for (int i = 0; i < 15 && lNotEnded; i++) {
                char c = (char) file.read(7);
                if (c == 0) {
                    lNotEnded = false;
                } else {
                    personalization += c;
                }
            }
            if (lNotEnded == true) {
                file.read(7);
            }
        }
    }
    
    private void addSetProperties(D2TxtFileItemProperties fullsetRow) {
        
        for (int x = 2; x < 6; x++) {
            if (fullsetRow.get("PCode" + x + "a").equals("")) { continue; }
            propCollection.addAll(D2TxtFile.propToStat(fullsetRow.get("PCode" + x + "a"),
                                                       fullsetRow.get("PMin" + x + "a"),
                                                       fullsetRow.get("PMax" + x + "a"),
                                                       fullsetRow.get("PParam" + x + "a"),
                                                       (20 + x)));
        }
        for (int x = 1; x < 9; x++) {
            if (fullsetRow.get("FCode" + x).equals("")) { continue; }
            propCollection.addAll(D2TxtFile.propToStat(fullsetRow.get("FCode" + x), fullsetRow.get("FMin" + x), fullsetRow.get("FMax" + x), fullsetRow.get("FParam" + x), 26));
        }
    }
    
    private void readExtend2(D2BitReader file) throws Exception {
        if (isTypeArmor()) {
            def = (short) (file.read(11) - 10); // -10 ???
            initDef = def;
            maxDur = (short) file.read(8);
            
            if (maxDur != 0) {
                curDur = (short) file.read(9);
            }
            
        } else if (isTypeWeapon()) {
            if (type.equals("tkni") || type.equals("taxe")
                    || type.equals("jave") || type.equals("ajav")) {
                isThrow = true;
            }
            maxDur = (short) file.read(8);
            
            if (maxDur != 0) {
                curDur = (short) file.read(9);
            }
            
            if ((D2TxtFile.WEAPONS.searchColumns("code", item_type)).get(
                    "1or2handed").equals("")
                    && !isThrow) {
                
                if ((D2TxtFile.WEAPONS.searchColumns("code", item_type)).get(
                        "2handed").equals("1")) {
                    whichHand = 2;
                    i1Dmg = new short[4];
                    i1Dmg[0] = i1Dmg[1] = Short.parseShort((D2TxtFile.WEAPONS
                            .searchColumns("code", item_type))
                                                                   .get("2handmindam"));
                    i1Dmg[2] = i1Dmg[3] = Short.parseShort((D2TxtFile.WEAPONS
                            .searchColumns("code", item_type))
                                                                   .get("2handmaxdam"));
                } else {
                    whichHand = 1;
                    i1Dmg = new short[4];
                    i1Dmg[0] = i1Dmg[1] = Short.parseShort((D2TxtFile.WEAPONS
                            .searchColumns("code", item_type)).get("mindam"));
                    i1Dmg[2] = i1Dmg[3] = Short.parseShort((D2TxtFile.WEAPONS
                            .searchColumns("code", item_type)).get("maxdam"));
                }
                
            } else {
                whichHand = 0;
                if (isThrow) {
                    i2Dmg = new short[4];
                    i2Dmg[0] = i2Dmg[1] = Short
                            .parseShort((D2TxtFile.WEAPONS.searchColumns(
                                    "code", item_type)).get("minmisdam"));
                    i2Dmg[2] = i2Dmg[3] = Short
                            .parseShort((D2TxtFile.WEAPONS.searchColumns(
                                    "code", item_type)).get("maxmisdam"));
                } else {
                    i2Dmg = new short[4];
                    i2Dmg[0] = i2Dmg[1] = Short
                            .parseShort((D2TxtFile.WEAPONS.searchColumns(
                                    "code", item_type)).get("2handmindam"));
                    i2Dmg[2] = i2Dmg[3] = Short
                            .parseShort((D2TxtFile.WEAPONS.searchColumns(
                                    "code", item_type)).get("2handmaxdam"));
                }
                i1Dmg = new short[4];
                i1Dmg[0] = i1Dmg[1] = Short.parseShort((D2TxtFile.WEAPONS
                        .searchColumns("code", item_type)).get("mindam"));
                i1Dmg[2] = i1Dmg[3] = Short.parseShort((D2TxtFile.WEAPONS
                        .searchColumns("code", item_type)).get("maxdam"));
            }
            
            if ("1".equals(itemType.get("stackable"))) {
                stackable = true;
                curDur = (short) file.read(9);
            }
        } else if (isTypeMisc()) {
            if ("1".equals(itemType.get("stackable"))) {
                stackable = true;
                curDur = (short) file.read(9);
            }
            
        }
        
        if (socketed) {
            socketNrTotal = (short) file.read(4);
        }
        
        int[] lSet = new int[5];
        
        if (quality == 5) {
            for (int x = 0; x < 5; x++) {
                lSet[x] = (int) file.read(1);
            }
        }
        
        if (jewel) {
            readProperties(file, 1);
        } else {
            readProperties(file, 0);
        }
        if (quality == 5) {
            for (int x = 0; x < 5; x++) {
                if (lSet[x] == 1) {
                    readProperties(file, x + 2);
                }
            }
        }
        if (runeWord) {
            readProperties(file, 0);
        }
    }
    
    private void applyAutomodLvl() {
        // modifies the level if the automod is higher
        if (automod_info == null) {
            return;
        }
        if (Integer.parseInt(automod_info.get("levelreq")) > reqLvl) {
            reqLvl = Integer.parseInt(automod_info.get("levelreq"));
        }
        
    }
    
    // MBR: unknown, but should be according to file format
    private void readTypes(D2BitReader file) {
        // charms ??
        if (isCharm()) {
            //			long lCharm1 = file.read(1);
            file.read(1);
            //			long lCharm2 = file.read(11);
            file.read(11);
            // System.err.println("Charm (?): " + lCharm1 );
            // System.err.println("Charm (?): " + lCharm2 );
        }
        
        // books / scrolls ??
        if ("tbk".equals(item_type) || "ibk".equals(item_type)) {
            //			long lTomb = file.read(5);
            file.read(5);
            // System.err.println("Tome ID: " + lTomb );
        }
        
        if ("tsc".equals(item_type) || "isc".equals(item_type)) {
            //			long lTomb = file.read(5);
            file.read(5);
            // System.err.println("Tome ID: " + lTomb );
        }
        
        // body ??
        if ("body".equals(item_type)) {
            //			long lMonster = file.read(10);
            file.read(10);
            // System.err.println("Monster ID: " + lMonster );
        }
    }
    
    private void readPropertiesPots(D2BitReader pfile) {
        
        String[] statsToRead = {"stat1", "stat2"};
        
        for (int x = 0; x < statsToRead.length; x = x + 1) {
            
            if ((D2TxtFile.MISC.searchColumns("code", item_type)).get(
                    statsToRead[x]).equals("")) { continue; }
            propCollection.add(new D2Prop(Integer.parseInt((D2TxtFile.ITEM_STAT_COST.searchColumns("Stat", (D2TxtFile.MISC
                    .searchColumns("code", item_type))
                    .get(statsToRead[x]))).get("ID")),
                                          new int[]{Integer.parseInt(((D2TxtFile.MISC
                                                  .searchColumns("code", item_type))
                                                  .get(statsToRead[x].replaceFirst("stat",
                                                                                   "calc"))))}, 0));
        }
    }
    
    private void readPropertiesGems(D2BitReader file) {
        //		RUNES ARE GEMS TOO!!!!
        String[][] gemHeaders = {{"weaponMod1", "weaponMod2", "weaponMod3"},
                                 {"helmMod1", "helmMod2", "helmMod3"},
                                 {"shieldMod1", "shieldMod2", "shieldMod3"}};
        
        for (int x = 0; x < gemHeaders.length; x++) {
            
            for (int y = 0; y < gemHeaders[x].length; y++) {
                
                if (D2TxtFile.GEMS.searchColumns("code", item_type).get(
                        gemHeaders[x][y] + "Code").equals("")) { continue; }
                propCollection.addAll(D2TxtFile.propToStat(D2TxtFile.GEMS
                                                                   .searchColumns("code", item_type).get(
                                gemHeaders[x][y] + "Code"), D2TxtFile.GEMS
                                                                   .searchColumns("code", item_type).get(
                                gemHeaders[x][y] + "Min"), D2TxtFile.GEMS
                                                                   .searchColumns("code", item_type).get(
                                gemHeaders[x][y] + "Max"), D2TxtFile.GEMS
                                                                   .searchColumns("code", item_type).get(
                                gemHeaders[x][y] + "Param"), (x + 7)));
            }
        }
    }
    
    private void readProperties(D2BitReader file, int qFlag) {
        
        int rootProp = (int) file.read(9);
        
        while (rootProp != 511) {
            
            propCollection.readProp(file, rootProp, qFlag);
            
            if (rootProp == 17) {
                propCollection.readProp(file, 18, qFlag);
            } else if (rootProp == 48) {
                propCollection.readProp(file, 49, qFlag);
            } else if (rootProp == 50) {
                propCollection.readProp(file, 51, qFlag);
            } else if (rootProp == 52) {
                propCollection.readProp(file, 53, qFlag);
            } else if (rootProp == 54) {
                propCollection.readProp(file, 55, qFlag);
                propCollection.readProp(file, 56, qFlag);
            } else if (rootProp == 57) {
                propCollection.readProp(file, 58, qFlag);
                propCollection.readProp(file, 59, qFlag);
            }
            rootProp = (int) file.read(9);
        }
        
    }
    
    private void applyItemMods() {
        
        int[] armourTriple = new int[]{0, 0, 0};
        int[] dmgTriple = new int[]{0, 0, 0, 0, 0};
        int[] durTriple = new int[]{0, 0};
        RequirementModifierAccumulator requirementModifierAccumulator = new RequirementModifierAccumulator();
        
        propCollection.applyOp(charLvl);
        
        for (int x = 0; x < propCollection.size(); x++) {
            if (((D2Prop) propCollection.get(x)).getQFlag() != 0 && ((D2Prop) propCollection.get(x)).getQFlag() != 12 && ((D2Prop) propCollection.get(x)).getQFlag() != 13 && ((D2Prop) propCollection
                    .get(x)).getQFlag() != 14 && ((D2Prop) propCollection
                    .get(x)).getQFlag() != 15 && ((D2Prop) propCollection.get(x)).getQFlag() != 16) { continue; }
            
            // +Dur
            if (((D2Prop) propCollection.get(x)).getPNum() == 73) {
                durTriple[0] = durTriple[0]
                        + ((D2Prop) propCollection.get(x)).getPVals()[0];
            }
            
            // Dur%
            if (((D2Prop) propCollection.get(x)).getPNum() == 75) {
                durTriple[1] = durTriple[1]
                        + ((D2Prop) propCollection.get(x)).getPVals()[0];
            }
            
            // +LvlReq
            if (((D2Prop) propCollection.get(x)).getPNum() == 92) {
                requirementModifierAccumulator.accumulateLevelRequirement(((D2Prop) propCollection.get(x)).getPVals()[0]);
            }
            
            // -Req
            if (((D2Prop) propCollection.get(x)).getPNum() == 91) {
                requirementModifierAccumulator.accumulatePercentRequirements(((D2Prop) propCollection.get(x)).getPVals()[0]);
            }
            
            // +Skills modify level
            if (((D2Prop) propCollection.get(x)).getPNum() == 97
                    || ((D2Prop) propCollection.get(x)).getPNum() == 107) {
                
                if (reqLvl < Integer.parseInt(D2TxtFile.SKILLS.searchColumns(
                        "skilldesc",
                        D2TxtFile.SKILL_DESC.getRow(
                                ((D2Prop) propCollection.get(x)).getPVals()[0]).get(
                                "skilldesc")).get("reqlevel"))) {
                    reqLvl = (Integer.parseInt(D2TxtFile.SKILLS.searchColumns(
                            "skilldesc",
                            D2TxtFile.SKILL_DESC.getRow(
                                    ((D2Prop) propCollection.get(x)).getPVals()[0])
                                                .get("skilldesc")).get("reqlevel")));
                }
            }
            
            if (isTypeArmor()) {
                
                // EDef
                if (((D2Prop) propCollection.get(x)).getPNum() == 16) {
                    armourTriple[0] = armourTriple[0]
                            + ((D2Prop) propCollection.get(x)).getPVals()[0];
                }
                
                // +Def
                if (((D2Prop) propCollection.get(x)).getPNum() == 31) {
                    armourTriple[1] = armourTriple[1]
                            + ((D2Prop) propCollection.get(x)).getPVals()[0];
                }
                
                // +Def/lvl
                if (((D2Prop) propCollection.get(x)).getPNum() == 214) {
                    armourTriple[2] = armourTriple[2]
                            + ((D2Prop) propCollection.get(x)).getPVals()[0];
                }
                
                if (isShield()) {
                    if (((D2Prop) propCollection.get(x)).getPNum() == 20) {
                        block = (short) (cBlock + ((D2Prop) propCollection.get(x))
                                .getPVals()[0]);
                    }
                }
                
            } else if (isTypeWeapon()) {
                
                // EDmg
                if (((D2Prop) propCollection.get(x)).getPNum() == 17) {
                    dmgTriple[0] = dmgTriple[0]
                            + ((D2Prop) propCollection.get(x)).getPVals()[0];
                }
                
                // MinDMg
                if (((D2Prop) propCollection.get(x)).getPNum() == 21) {
                    dmgTriple[1] = dmgTriple[1]
                            + ((D2Prop) propCollection.get(x)).getPVals()[0];
                    if (((D2Prop) propCollection.get(x)).getFuncN() == 31) {
                        dmgTriple[2] = dmgTriple[2]
                                + ((D2Prop) propCollection.get(x)).getPVals()[1];
                    }
                }
                
                // MaxDmg
                if (((D2Prop) propCollection.get(x)).getPNum() == 22) {
                    dmgTriple[2] = dmgTriple[2]
                            + ((D2Prop) propCollection.get(x)).getPVals()[0];
                }
                
                // MaxDmg/Lvl
                if (((D2Prop) propCollection.get(x)).getPNum() == 218) {
                    dmgTriple[3] = dmgTriple[3]
                            + ((D2Prop) propCollection.get(x)).getPVals()[0];
                }
                
                // MaxDmg%/lvl
                if (((D2Prop) propCollection.get(x)).getPNum() == 219) {
                    dmgTriple[4] = dmgTriple[4]
                            + ((D2Prop) propCollection.get(x)).getPVals()[0];
                }
            }
        }
        
        reqLvl = reqLvl + requirementModifierAccumulator.getLevelRequirement();
        double percentReqirementsModifier = requirementModifierAccumulator.getPercentRequirements() / (double) 100;
        reqDex = reqDex + ((int) (reqDex * percentReqirementsModifier));
        reqStr = reqStr + ((int) (reqStr * percentReqirementsModifier));
        
        if (isTypeWeapon()) {
            
            if (ethereal) {
                i1Dmg[0] = i1Dmg[1] = (short) Math
                        .floor((((double) i1Dmg[1] / (double) 100) * (double) 50)
                                       + i1Dmg[1]);
                i1Dmg[2] = i1Dmg[3] = (short) Math
                        .floor((((double) i1Dmg[3] / (double) 100) * (double) 50)
                                       + i1Dmg[3]);
                
                if (whichHand == 0) {
                    i2Dmg[0] = i2Dmg[1] = (short) Math
                            .floor((((double) i2Dmg[1] / (double) 100) * (double) 50)
                                           + i2Dmg[1]);
                    i2Dmg[2] = i2Dmg[3] = (short) Math
                            .floor((((double) i2Dmg[3] / (double) 100) * (double) 50)
                                           + i2Dmg[3]);
                }
            }
            
            i1Dmg[1] = (short) Math
                    .floor((((double) i1Dmg[1] / (double) 100) * dmgTriple[0])
                                   + (i1Dmg[1] + dmgTriple[1]));
            i1Dmg[3] = (short) Math
                    .floor((((double) i1Dmg[3] / (double) 100) * (dmgTriple[0] + dmgTriple[4]))
                                   + (i1Dmg[3] + (dmgTriple[2] + dmgTriple[3])));
            
            if (whichHand == 0) {
                i2Dmg[1] = (short) Math.floor((((double) i2Dmg[1] / (double) 100) * dmgTriple[0])
                                                      + (i2Dmg[1] + dmgTriple[1]));
                i2Dmg[3] = (short) Math.floor((((double) i2Dmg[3] / (double) 100) * (dmgTriple[0] + dmgTriple[4]))
                                                      + (i2Dmg[3] + (dmgTriple[2] + dmgTriple[3])));
            }
            if (i1Dmg[1] > i1Dmg[3]) {
                i1Dmg[3] = (short) (i1Dmg[1] + 1);
            }
            
        } else if (isTypeArmor()) {
            def = (short) Math
                    .floor((((double) initDef / (double) 100) * armourTriple[0])
                                   + (initDef + (armourTriple[1] + armourTriple[2])));
        }
        
        maxDur = (short) Math
                .floor((((double) maxDur / (double) 100) * durTriple[1])
                               + (maxDur + durTriple[0]));
        
    }
    
    private boolean checkFlag(int bit) {
        if (((flags >>> (32 - bit)) & 1) == 1) { return true; } else { return false; }
    }
    
    private int getReq(String req) {
        if (req != null) {
            String lReq = req.trim();
            if (!lReq.equals("") && !lReq.equals("0")) {
                try {
                    return Integer.parseInt(lReq);
                } catch (Exception ex) {
                    // do nothing, no req
                }
            }
        }
        return -1;
    }
    
    private String getExStr() {
        return " (" + itemName + ", " + fP + ")";
    }
    
    private boolean isBodyLocation(String location) {
        if (body) {
            if (location.equals(bodyLoc1)) {
                return true;
            }
            if (location.equals(bodyLoc2)) {
                return true;
            }
        }
        return false;
    }
    
    private String htmlStrip(StringBuffer htmlString) {
        String dumpStr = htmlString.toString().replaceAll("<br>&#10;", System.getProperty("line.separator"));
        dumpStr = dumpStr.replaceAll("&#32;", "");
        return dumpStr.replaceAll("<[^>]*>", "");
    }
    
    public String itemDumpHtml(boolean extended) {
        return generatePropString(extended).toString();
    }
    
    public String itemDump(boolean extended) {
        return htmlStrip(generatePropString(extended));
    }
    
    private StringBuffer generatePropString(boolean extended) {
        StringBuffer propString = new StringBuffer("<html>");
        propString.append(generatePropStringNoHtmlTags(extended));
        return propString.append("</html>");
    }
    
    private StringBuffer generatePropStringNoHtmlTags(boolean extended) {
        propCollection.tidy();
        StringBuffer dispStr = new StringBuffer("<center>");
        String base = (Integer.toHexString(Color.white.getRGB())).substring(2);
        String rgb = (Integer.toHexString(getItemColor().getRGB())).substring(2);
        if (personalization == null) {
            dispStr.append("<font color=\"#" + base + "\">" + "<font color=\"#" + rgb + "\">" + itemName + "</font>" + "<br>&#10;");
        } else {
            dispStr.append("<font color=\"#" + base + "\">" + "<font color=\"#" + rgb + "\">" + personalization + "'s " + itemName + "</font>" + "<br>&#10;");
        }
        if (!baseItemName.equals(itemName)) { dispStr.append("<font color=\"#" + rgb + "\">" + baseItemName + "</font>" + "<br>&#10;"); }
        if (isRuneWord()) {
            dispStr.append("<font color=\"#" + rgb + "\">");
            for (int x = 0; x < socketedItems.size(); x++) {
                dispStr.append((((D2Item) socketedItems.get(x)).getItemName()), 0, ((D2Item) socketedItems.get(x)).getItemName().length() - 5);
            }
            dispStr.append("</font><br>&#10;");
        }
        if (isTypeWeapon() || isTypeArmor()) {
            if (isTypeWeapon()) {
                if (whichHand == 0) {
                    if (isThrow) {
                        dispStr.append("Throw Damage: " + i2Dmg[1] + " - " + i2Dmg[3] + "<br>&#10;");
                        dispStr.append("One Hand Damage: " + i1Dmg[1] + " - " + i1Dmg[3] + "<br>&#10;");
                    } else {
                        dispStr.append("One Hand Damage: " + i1Dmg[1] + " - " + i1Dmg[3] + "<br>&#10;");
                        dispStr.append("Two Hand Damage: " + i2Dmg[1] + " - " + i2Dmg[3] + "<br>&#10;");
                    }
                } else if (whichHand == 1) {
                    dispStr.append("One Hand Damage: " + i1Dmg[1] + " - " + i1Dmg[3] + "<br>&#10;");
                } else {
                    dispStr.append("Two Hand Damage: " + i1Dmg[1] + " - " + i1Dmg[3] + "<br>&#10;");
                }
            } else if (isTypeArmor()) {
                dispStr.append("Defense: " + def + "<br>&#10;");
                if (isShield()) {
                    dispStr.append("Chance to Block: " + block + "<br>&#10;");
                }
            }
            if (isStackable()) {
                dispStr.append("Quantity: " + curDur + "<br>&#10;");
            } else {
                if (maxDur == 0) {
                    dispStr.append("Indestructible" + "<br>&#10;");
                } else {
                    dispStr.append("Durability: " + curDur + " of " + maxDur + "<br>&#10;");
                }
            }
        }
        if (reqLvl > 0) { dispStr.append("Required Level: " + reqLvl + "<br>&#10;"); }
        if (reqStr > 0) { dispStr.append("Required Strength: " + reqStr + "<br>&#10;"); }
        if (reqDex > 0) { dispStr.append("Required Dexterity: " + reqDex + "<br>&#10;"); }
        if (fP != null) { dispStr.append("Fingerprint: " + fP + "<br>&#10;"); }
        if (gUID != null) { dispStr.append("GUID: " + gUID + "<br>&#10;"); }
        if (ilvl != 0) { dispStr.append("Item Level: " + ilvl + "<br>&#10;"); }
        dispStr.append("Version: " + get_version() + "<br>&#10;");
        if (!identified) { dispStr.append("Unidentified" + "<br>&#10;"); }
        
        dispStr.append(getItemPropertyString());
        
        if (extended) {
            if (isSocketed()) {
                
                if (socketedItems != null) {
                    dispStr.append("<br>&#10;");
                    for (int x = 0; x < socketedItems.size(); x = x + 1) {
                        if (socketedItems.get(x) != null) {
                            dispStr.append(((D2Item) socketedItems.get(x)).generatePropStringNoHtmlTags(false));
                            if (x != socketedItems.size() - 1) {
                                dispStr.append("<br>&#10;");
                            }
                        }
                    }
                }
            }
        }
        
        return dispStr.append("</center>");
    }
    
    private StringBuffer getItemPropertyString() {
        
        StringBuffer dispStr = new StringBuffer();
        
        if (isJewel()) {
            dispStr.append(propCollection.generateDisplay(1, charLvl));
        } else {
            dispStr.append(propCollection.generateDisplay(0, charLvl));
        }
        if (isGem() || isRune()) {
            dispStr.append("Weapons: ");
            dispStr.append(propCollection.generateDisplay(7, charLvl));
            dispStr.append("Armor: ");
            dispStr.append(propCollection.generateDisplay(8, charLvl));
            dispStr.append("Shields: ");
            dispStr.append(propCollection.generateDisplay(9, charLvl));
        }
        if (quality == 5) {
            
            for (int x = 12; x < 17; x++) {
                StringBuffer setBuf = propCollection.generateDisplay(x, charLvl);
                if (setBuf.length() > 29) {
                    dispStr.append("<font color=\"red\">Set (" + (x - 10) + " items): ");
                    dispStr.append(setBuf);
                    dispStr.append("</font>");
                }
            }
            
            for (int x = 2; x < 7; x++) {
                StringBuffer setBuf = propCollection.generateDisplay(x, charLvl);
                if (setBuf.length() > 29) {
                    dispStr.append("Set (" + x + " items): ");
                    dispStr.append(setBuf);
                }
            }
        }
        if (ethereal) {
            dispStr.append("<font color=\"#4850b8\">Ethereal</font><br>&#10;");
        }
        if (socketNrTotal > 0) {
            dispStr.append(socketNrTotal + " Sockets (" + socketNrFilled + " used)<br>&#10;");
            if (socketedItems != null) {
                for (int i = 0; i < socketedItems.size(); i++) {
                    dispStr.append("Socketed: " + ((D2Item) socketedItems.get(i)).getItemName() + "<br>&#10;");
                }
            }
        }
        
        if (quality == 5) {
            dispStr.append("<br>&#10;");
            for (int x = 32; x < 36; x++) {
                StringBuffer setBuf = propCollection.generateDisplay(x, charLvl);
                if (setBuf.length() > 29) {
                    dispStr.append("<font color=\"red\">(" + (x - 30) + " items): ");
                    dispStr.append(setBuf);
                    dispStr.append("</font>");
                }
            }
            
            StringBuffer setBuf = propCollection.generateDisplay(36, charLvl);
            if (setBuf.length() > 33) {
                dispStr.append(setBuf);
            }
            
        }
        
        return dispStr;
    }
    
    public void toWriter(PrintWriter pw) {
        pw.println();
        pw.print(itemDump(true));
    }
    
    public boolean isBodyLArm() {
        return isBodyLocation("larm");
    }
    
    public boolean isBodyRRin() {
        return isBodyLocation("rrin");
    }
    
    public boolean isBodyLRin() {
        return isBodyLocation("lrin");
    }
    
    public boolean isWeaponType(D2WeaponTypes type) {
        if (typeWeapon) {
            if (type.isType(this.type)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isBodyLocation(D2BodyLocations location) {
        if (body) {
            if (location.getLocation().equals(bodyLoc1)) {
                return true;
            }
            if (location.getLocation().equals(bodyLoc2)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isCharm() {
        return (smallCharm || largeCharm || grandCharm);
    }
    
    // setter for the row
    // necessary for moving items
    public void setRow(short r) {
        item.set_byte_pos(7);
        item.skipBits(13);
        item.write(r, 4);
        row = r;
    }
    
    // setter for the column
    // necessary for moving items
    public void setCol(short c) {
        item.set_byte_pos(7);
        item.skipBits(9);
        item.write(c, 4);
        col = c;
    }
    
    public void setLocation(short l) {
        item.set_byte_pos(7);
        item.skipBits(2);
        item.write(l, 3);
        location = l;
    }
    
    public void set_body_position(short bp) {
        item.set_byte_pos(7);
        item.skipBits(5);
        item.write(bp, 4);
        bodyPosition = bp;
    }
    
    public void setPanel(short p) {
        item.set_byte_pos(7);
        item.skipBits(17);
        item.write(p, 3);
        panel = p;
    }
    
    public String get_version() {
        
        if (version == 0) {
            return "Legacy (pre 1.08)";
        }
        
        if (version == 1) {
            return "Classic";
        }
        //2 is another version perhaps?
        if (version == 100) {
            return "Expansion";
        }
        
        if (version == 101) {
            return "Expansion 1.10+";
        }
        
        return "UNKNOWN";
    }
    
    public byte[] getBytes() {
        return item.getFileContent();
    }
    
    public int getItemLength() {
        return item.get_length();
    }
    
    @Override
    public String getFingerprint() {
        return fP;
    }
    
    public String getILvl() {
        return Short.toString(ilvl);
    }
    
    public Color getItemColor() {
        if (isUnique()) {
            // return Color.yellow.darker().darker();
            return new Color(255, 222, 173);
        }
        if (isSet()) {
            return Color.green.darker();
        }
        if (isRare()) {
            return Color.yellow.brighter();
        }
        if (isMagical()) {
            return new Color(72, 118, 255);
        }
        if (isRune()) {
            return Color.red;
        }
        if (isCrafted()) {
            return Color.orange;
        }
        if (isRuneWord()) {
            return new Color(255, 222, 173);
        }
        if (isEthereal() || isSocketed()) {
            return Color.gray;
        }
        return Color.white;
    }
    
    public boolean isShield() {
        return type != null && (type.equals("ashd") || type.equals("shie") || type.equals("head"));
    }
    
    public boolean isNormal() {
        return !(isMagical() || isRare() || isCrafted() || isRuneWord() || isRune() || isSet() || isUnique());
    }
    
    public boolean isSocketFiller() {
        return isRune() || isJewel() || isGem();
    }
    
    public boolean isRune() {
        return getRuneCode() != null;
    }
    
    public String getRuneCode() {
        if (itemType != null) {
            if ("rune".equals(itemType.get("type"))) {
                return itemType.get("code");
            }
        }
        return null;
    }
    
    public boolean isCursorItem() {
        if (location != 0 && location != 2) {
            if (bodyPosition == 0) {
                // System.err.println("location: " + location );
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int compareTo(D2Item other) {
        return ComparisonChain.start()
                              .compare(itemName, other.itemName, Comparator.nullsFirst(Comparator.naturalOrder()))
                              .result();
    }
    
    public boolean isCharacterItem() {
        
        //Belt or equipped
        if (getLocation() == 1 || getLocation() == 2) {
            return true;
        } else if (getLocation() == 0) {
            switch (getPanel()) {
                case 1:
                case 4:
                case 5:
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
        
    }
    
    public boolean isEquipped() {
        return getLocation() == 1 || (getPanel() == 1 && isCharm());
    }
    
    public boolean isEquipped(int wepSlot) {
        
        if (getLocation() == 1) {
            
            if (!isTypeWeapon() && !isShield()) { return true; }
            if (wepSlot == 0) {
                if (getBodyPosition() == 4 || getBodyPosition() == 5) { return true; }
            } else if (wepSlot == 1) {
                if (getBodyPosition() == 11 || getBodyPosition() == 12) { return true; }
            }
            return false;
        } else if (getPanel() == 1 && isCharm()) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean statModding() {
        
        if (jewel || gem || rune) {
            return false;
        } else {
            
            return true;
        }
    }
    
    public String getPreSuf() {
        
        String retStr = "";
        for (int x = 0; x < rare_prefixes.length; x++) {
            
            if (rare_prefixes[x] > 1) {
                
                retStr = retStr
                        + D2TblFile.getString(D2TxtFile.PREFIX.getRow(
                        rare_prefixes[x]).get("Name")) + " ";
            }
        }
        
        retStr = retStr + baseItemName + " ";
        
        for (int x = 0; x < rare_suffixes.length; x++) {
            
            if (rare_suffixes[x] > 1) {
                
                retStr = retStr
                        + D2TblFile.getString(D2TxtFile.SUFFIX.getRow(
                        rare_suffixes[x]).get("Name")) + " ";
            }
        }
        
        return retStr;
    }
    
    public boolean conforms(String prop, int val, boolean min) {
        
        String dumpStr = itemDump(true);
        
        if (dumpStr.toLowerCase().indexOf(prop.toLowerCase()) != -1) {
            
            if (val == -1337) {
                return true;
            }
            Pattern propertyLinePattern = Pattern.compile("(\\n.*" + prop.toLowerCase() + ".*\\n)", Pattern.UNIX_LINES);
            Matcher propertyPatternMatcher = propertyLinePattern.matcher("\n" + dumpStr.toLowerCase() + "\n");
            while (propertyPatternMatcher.find()) {
                Pattern pat = Pattern.compile("[^(?](\\d+)");
                Matcher mat = pat.matcher(propertyPatternMatcher.group());
                while (mat.find()) {
                    if (mat.groupCount() > 0) {
                        if (min == true) {
                            if (Integer.parseInt(mat.group(1)) >= val) {
                                
                                return true;
                            }
                        } else {
                            if (Integer.parseInt(mat.group(1)) <= val) {
                                
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public int getBlock() {
        return cBlock;
    }
    
    public boolean isABelt() {
        return type.equals("belt");
    }
    
    public void refreshItemMods() {
        if (isTypeArmor() || isTypeWeapon()) {
            applyItemMods();
        }
    }
    
    public String getBaseItemName() {
        if (!itemName.equals(baseItemName)) {
            return baseItemName;
        }
        
        return "";
    }
    
    public boolean isMoveable() {
        
        if (getLocation() == 0 && getPanel() == 1 &&
                (getItemName().toLowerCase().equals("horadric cube") || isCharm() || getItemName().toLowerCase().equals("key") || getItemName().toLowerCase().contains("tome of"))) {
            //Inv
        } else if (getLocation() == 2) {
            //Belt
        } else if (getLocation() == 0 && getPanel() == 5 && getItemName().toLowerCase().equals("horadric cube")) {
            //Stash
        } else if (getLocation() == 1) {
            //equipped
        } else {
            return true;
        }
        return false;
    }
    
}
