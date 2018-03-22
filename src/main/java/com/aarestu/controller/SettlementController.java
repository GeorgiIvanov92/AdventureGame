package com.aarestu.controller;

import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class SettlementController {
	Hero hero;
	String theBadCookie="";
	String resource="";
	int count;
	Item tempItem;
	Item tempItem2;
	Item tempItem3;
	Item tempItem4;
	Item tempItem5;
	ArrayList<Item> items=new ArrayList<Item>();
	ArrayList<Item> shop;
	ArrayList<Item> getFiveItems(int min, int max,ArrayList<Item> theItems) {
		for(int i=1; i<=5; i++)
		{
	    int range = (max - min) + 1;
	    int theIndex=(int)(Math.random() * range) + min;
	    if(theItems.contains(items.get(theIndex))) {
		    i--;
		    continue;
	    }
	    theItems.add(items.get(theIndex));
	    //items.remove(theIndex);
		}
	    return theItems;
	}

	@RequestMapping(value = "/skipSettlement", method = RequestMethod.GET)
	String hello(ModelMap model, HttpServletResponse response, @CookieValue("hero") String heroCookie,
			@CookieValue("resource") String resourceCookie, @CookieValue("bossState") String bossState) {
		if (!bossState.equals("dead")) {
			hero = Hero.fromCookie(heroCookie);
			hero.enemyEncountersLeft += 1;
			Cookie theHero=hero.createCookie();
			theHero.setPath("/");
			theHero.setMaxAge(60*60*24*2);
			response.addCookie(theHero);
			model.addAttribute("resource", resourceCookie);
			model.addAttribute("enemiesLeft",hero.enemyEncountersLeft);

			Cookie d = new Cookie("bossState", "dead");
			d.setPath("/");
			d.setMaxAge(60 * 60 * 24 * 2);
			response.addCookie(d);
		}
		return "skippedSettlement";
	}

	@RequestMapping(value = "/sleep", method = RequestMethod.GET)
	String sleep(ModelMap model, HttpServletResponse response, @CookieValue("resource") String resourceCookie,
			@CookieValue("bossState") String bossState, @CookieValue("hero") String heroCookie) {
		if (!bossState.equals("dead")) {
			hero = Hero.fromCookie(heroCookie);
			hero.enemyEncountersLeft -= 1;
			model.addAttribute("enemiesLeft", hero.enemyEncountersLeft);
			model.addAttribute("resource", resourceCookie);
			if (hero.hp + 15 <= hero.maxHp) {
				hero.hp += 15;
			} else {
				hero.hp = hero.maxHp;
			}
			if (hero.mana+5<=hero.maxMana)
			{
				hero.mana+=5;
			}else {
				hero.mana=hero.maxMana;
			}

			Cookie d = new Cookie("bossState", "dead");
			Cookie cc = hero.createCookie();
			cc.setPath("/");
			cc.setMaxAge(60 * 60 * 24 * 2);
			response.addCookie(cc);
			d.setPath("/");
			d.setMaxAge(60 * 60 * 24 * 2);
			model.addAttribute("sleep", "You decide to sleep Restoring 15 Health and 5 Mana");
			response.addCookie(d);
		}
		return "leaveSettlement";
	}

	@RequestMapping(value="/leaveSettlement",method=RequestMethod.GET)
	String bye(ModelMap model,HttpServletResponse response,@CookieValue("leftEnemies") String fooCookie,@CookieValue("resource") String resourceCookie,@CookieValue("bossState")String bossState)
	{
		

		model.addAttribute("resource",resourceCookie);
		
		Cookie d=new Cookie("bossState","dead");
		d.setPath("/");
		d.setMaxAge(60*60*24*2);
		response.addCookie(d);
		
		return "leaveSettlement";
	}
	
	@RequestMapping(value="/settlement", method=RequestMethod.GET)
	String insideSettlement(ModelMap model,HttpServletResponse response,@CookieValue("hero")String heroCookie,@CookieValue("resource") String resourceCookie,@CookieValue("bossState")String bossStateCookie,@CookieValue("leftEnemies")String leftEnemiesCookie)
	{
		if(Integer.parseInt(leftEnemiesCookie)<0)
		{
			leftEnemiesCookie="0";
		}
		hero=Hero.fromCookie(heroCookie);
		model.addAttribute("leftEnemies",leftEnemiesCookie);
		model.addAttribute("resource",resourceCookie);
		Item smallPotion = new Item("Small Health Potion", 9).setCurrentHealth(25);
		Item magicBoots = new Item("Magic Boots", 17).setMagicResist(2);
		Item woodenShield = new Item("Wooden Shield", 17).setArmor(2);
		Item woodenSword = new Item("Wooden Sword", 20).setAttackMin(2).setAttackMax(2);
		Item smallVest = new Item("Small Vest", 14).setHealthLimit(15).setCurrentHealth(15);
		Item swordSharpener = new Item("Sword Sharpener", 17).setCritChance(3);
		Item woodenBow = new Item("Woden Bow", 20).setAttackMax(4);
		Item strawHat = new Item("Straw Hat", 14).setArmor(1).setCurrentHealth(15);
		Item ironSword = new Item("Iron Sword", 33).setAttackMin(4).setAttackMax(4);
		Item mediumPotion = new Item("Medium Health Potion", 16).setCurrentHealth(50);
		Item energyBoost = new Item("Energy Boost", 24).setCurrentHealth(10).setAttackMin(1).setAttackMax(1).setArmor(1).setMagicResist(1).setCritChance(2);
		Item leatherArmor = new Item("Leather Armor", 27).setArmor(2).setMagicResist(2);
		Item vitalityBoost = new Item("Vitality Boost", 19).setCurrentHealth(20).setAttackMax(2);
		Item handMadeArrows = new Item("Hand-made Arrows", 14).setAttackMin(1).setAttackMax(1).setCritChance(1);
		Item kunai = new Item("Kunai", 13).setAttackMax(2);
		Item largePotion=new Item("Large Health Potion", 29).setCurrentHealth(100);
		Item linenShirt=new Item("Linen Shirt",16).setArmor(1).setMagicResist(1);
		Item mediumVest=new Item("Medium Vest",24).setCurrentHealth(30).setHealthLimit(30);
		Item smallManaPotion=new Item("Small Mana Potion",8).setMana(12);
		Item mediumManaPotion=new Item("Medium Mana Potion",15).setMana(25);
		Item largeManaPotion=new Item("Large Mana Potion",28).setMana(50);
		Item smallMixedPotion=new Item("Small Mixed Potion",16).setCurrentHealth(25).setMana(12);
		Item mediumMixedPotion=new Item("Medium Mixed Potion",29).setCurrentHealth(50).setMana(25);
		Item smallCape=new Item("Small Cape",18).setMaxMana(10).setMana(10);
		Item mediumCape=new Item("Medium Cape",32).setMaxMana(20).setMana(20);
		Item woodenStaff=new Item("Wooden Staff",32).setMana(5).setMaxMana(15).setMagicResist(2);
		Item vitalityNecklace=new Item("Vitality Necklace",30).setCurrentHealth(15).setHealthLimit(30).setMaxMana(15).setMana(10);
		model.addAttribute("message", hero.createDisplayText());
		items.add(smallPotion);
		items.add(magicBoots);
		items.add(woodenShield);
		items.add(woodenSword);
		items.add(smallVest);
		items.add(swordSharpener);
		items.add(woodenBow);
		items.add(strawHat);
		items.add(ironSword);
		items.add(mediumPotion);
		items.add(energyBoost);
		items.add(leatherArmor);
		items.add(kunai);
		items.add(handMadeArrows);
		items.add(vitalityBoost);
		items.add(largePotion);
		items.add(linenShirt);
		items.add(mediumVest);
		items.add(smallManaPotion);
		items.add(mediumManaPotion);
		items.add(largeManaPotion);
		items.add(smallMixedPotion);
		items.add(mediumMixedPotion);
		items.add(smallCape);
		items.add(mediumCape);
		items.add(woodenStaff);
		items.add(vitalityNecklace);

		if (!bossStateCookie.equals("shopping")) {
			shop = new ArrayList<Item>();
			getFiveItems(0, items.size() - 1, shop);
		}
		
			 tempItem=shop.get(0);
			model.addAttribute("name",tempItem.name+": ");
			if(tempItem.currentHealth!=0)
			{
				model.addAttribute("currentHealth","Current Health +"+String.valueOf(tempItem.currentHealth)+" ");
			}
			if(tempItem.healthLimit!=0)
			{
				model.addAttribute("maxHealth","Max Health +"+String.valueOf(tempItem.healthLimit)+" ");
			}
			if(tempItem.mana!=0)
			{
				model.addAttribute("mana","Mana +"+String.valueOf(tempItem.mana)+" ");
			}
			if(tempItem.maxMana!=0)
			{
				model.addAttribute("maxMana","Max Mana +"+String.valueOf(tempItem.maxMana)+" ");
			}
			if(tempItem.attackMin!=0)
			{
				model.addAttribute("attackMin","Attack Minimum +"+String.valueOf(tempItem.attackMin)+" ");
			}
			if(tempItem.attackMax!=0)
			{
				model.addAttribute("attackMax","Attack Maximum +"+String.valueOf(tempItem.attackMax)+" ");
			}
			if(tempItem.armor!=0)
			{
				model.addAttribute("armor","Armor +"+String.valueOf(tempItem.armor)+" ");
			}
			if(tempItem.magicResist!=0)
			{
				model.addAttribute("magicResist","Magic Resist +"+String.valueOf(tempItem.magicResist)+" ");
			}
			if(tempItem.critChance!=0)
			{
				model.addAttribute("critChance","Critical Chance +"+String.valueOf(tempItem.critChance)+" ");
			}
			model.addAttribute("costsGold","Gold -"+String.valueOf(tempItem.costsGold));
		
			tempItem2=shop.get(1);
			model.addAttribute("secondName",tempItem2.name+": ");
			if(tempItem2.currentHealth!=0)
			{
				model.addAttribute("secondCurrentHealth","Current Health +"+String.valueOf(tempItem2.currentHealth)+" ");
			}
			if(tempItem2.healthLimit!=0)
			{
				model.addAttribute("secondMaxHealth","Max Health +"+String.valueOf(tempItem2.healthLimit)+" ");
			}
			
			if(tempItem2.mana!=0)
			{
				model.addAttribute("secondMana","Mana +"+String.valueOf(tempItem2.mana)+" ");
			}
			if(tempItem2.maxMana!=0)
			{
				model.addAttribute("secondMaxMana","Max Mana +"+String.valueOf(tempItem2.maxMana)+" ");
			}
			if(tempItem2.attackMin!=0)
			{
				model.addAttribute("secondAttackMin","Attack Minimum +"+String.valueOf(tempItem2.attackMin)+" ");
			}
			if(tempItem2.attackMax!=0)
			{
				model.addAttribute("secondAttackMax","Attack Maximum +"+String.valueOf(tempItem2.attackMax)+" ");
			}
			if(tempItem2.armor!=0)
			{
				model.addAttribute("secondArmor","Armor +"+String.valueOf(tempItem2.armor)+" ");
			}
			if(tempItem2.magicResist!=0)
			{
				model.addAttribute("secondMagicResist","Magic Resist +"+String.valueOf(tempItem2.magicResist)+" ");
			}
			if(tempItem2.critChance!=0)
			{
				model.addAttribute("secondCritChance","Critical Chance +"+String.valueOf(tempItem2.critChance)+" ");
			}
			model.addAttribute("secondCostsGold","Gold -"+String.valueOf(tempItem2.costsGold)+" ");
			
			tempItem3=shop.get(2);
			model.addAttribute("thirdName",tempItem3.name+": ");
			if(tempItem3.currentHealth!=0)
			{
				model.addAttribute("thirdCurrentHealth","Current Health +"+String.valueOf(tempItem3.currentHealth)+" ");
			}
			if(tempItem3.healthLimit!=0)
			{
				model.addAttribute("thirdMaxHealth","Max Health +"+String.valueOf(tempItem3.healthLimit)+" ");
			}
			if(tempItem3.mana!=0)
			{
				model.addAttribute("thirdMana","Mana +"+String.valueOf(tempItem3.mana)+" ");
			}
			if(tempItem3.maxMana!=0)
			{
				model.addAttribute("thirdMaxMana","Max Mana +"+String.valueOf(tempItem3.maxMana)+" ");
			}
			if(tempItem3.attackMin!=0)
			{
				model.addAttribute("thirdAttackMin","Attack Minimum +"+String.valueOf(tempItem3.attackMin)+" ");
			}
			if(tempItem3.attackMax!=0)
			{
				model.addAttribute("thirdAttackMax","Attack Maximum +"+String.valueOf(tempItem3.attackMax)+" ");
			}
			if(tempItem3.armor!=0)
			{
				model.addAttribute("thirdArmor","Armor +"+String.valueOf(tempItem3.armor)+" ");
			}
			if(tempItem3.magicResist!=0)
			{
				model.addAttribute("thirdMagicResist","Magic Resist +"+String.valueOf(tempItem3.magicResist)+" ");
			}
			if(tempItem3.critChance!=0)
			{
				model.addAttribute("thirdCritChance","Critical Chance +"+String.valueOf(tempItem3.critChance)+" ");
			}
			model.addAttribute("thirdCostsGold","Gold -"+String.valueOf(tempItem3.costsGold)+" ");
			
			tempItem4=shop.get(3);
			model.addAttribute("fourthName",tempItem4.name+": ");
			if(tempItem4.currentHealth!=0)
			{
				model.addAttribute("fourthCurrentHealth","Current Health +"+String.valueOf(tempItem4.currentHealth)+" ");
			}
			if(tempItem4.healthLimit!=0)
			{
				model.addAttribute("fourthMaxHealth","Max Health +"+String.valueOf(tempItem4.healthLimit)+" ");
			}
			if(tempItem4.mana!=0)
			{
				model.addAttribute("fourthMana","Mana +"+String.valueOf(tempItem4.mana)+" ");
			}
			if(tempItem4.maxMana!=0)
			{
				model.addAttribute("fourthMaxMana","Max Mana +"+String.valueOf(tempItem4.maxMana)+" ");
			}
			if(tempItem4.attackMin!=0)
			{
				model.addAttribute("fourthAttackMin","Attack Minimum +"+String.valueOf(tempItem4.attackMin)+" ");
			}
			if(tempItem4.attackMax!=0)
			{
				model.addAttribute("fourthAttackMax","Attack Maximum +"+String.valueOf(tempItem4.attackMax)+" ");
			}
			if(tempItem4.armor!=0)
			{
				model.addAttribute("fourthArmor","Armor +"+String.valueOf(tempItem4.armor)+" ");
			}
			if(tempItem4.magicResist!=0)
			{
				model.addAttribute("fourthMagicResist","Magic Resist +"+String.valueOf(tempItem4.magicResist)+" ");
			}
			if(tempItem4.critChance!=0)
			{
				model.addAttribute("fourthCritChance","Critical Chance +"+String.valueOf(tempItem4.critChance)+" ");
			}
			model.addAttribute("fourthCostsGold","Gold -"+String.valueOf(tempItem4.costsGold)+" ");
			


			
			
			
			
			tempItem5=shop.get(4);
			model.addAttribute("fifthName",tempItem5.name+": ");
			if(tempItem5.currentHealth!=0)
			{
				model.addAttribute("fifthCurrentHealth","Current Health +"+String.valueOf(tempItem5.currentHealth)+" ");
			}
			if(tempItem5.healthLimit!=0)
			{
				model.addAttribute("fifthMaxHealth","Max Health +"+String.valueOf(tempItem5.healthLimit)+" ");
			}
			if(tempItem5.mana!=0)
			{
				model.addAttribute("fifthMana","Mana +"+String.valueOf(tempItem5.mana)+" ");
			}
			if(tempItem5.maxMana!=0)
			{
				model.addAttribute("fifthMaxMana","Max Mana +"+String.valueOf(tempItem5.maxMana)+" ");
			}
			if(tempItem5.attackMin!=0)
			{
				model.addAttribute("fifthAttackMin","Attack Minimum +"+String.valueOf(tempItem5.attackMin)+" ");
			}
			if(tempItem5.attackMax!=0)
			{
				model.addAttribute("fifthAttackMax","Attack Maximum +"+String.valueOf(tempItem5.attackMax)+" ");
			}
			if(tempItem5.armor!=0)
			{
				model.addAttribute("fifthArmor","Armor +"+String.valueOf(tempItem5.armor)+" ");
			}
			if(tempItem5.magicResist!=0)
			{
				model.addAttribute("fifthMagicResist","Magic Resist +"+String.valueOf(tempItem5.magicResist)+" ");
			}
			if(tempItem5.critChance!=0)
			{
				model.addAttribute("fifthCritChance","Critical Chance +"+String.valueOf(tempItem5.critChance)+" ");
			}
			model.addAttribute("fifthCostsGold","Gold -"+String.valueOf(tempItem5.costsGold)+" ");
			
		items.clear();
		Cookie boss=new Cookie("bossState","shopping");
		boss.setPath("/");
		boss.setMaxAge(60*60*24*2);
		response.addCookie(boss);
		return "settlement";
	}
	@RequestMapping(value="/item1",method=RequestMethod.GET)
	String theItem(ModelMap model,HttpServletResponse response,@CookieValue("resource")String resourceCookie)
	{
		if(hero.gold-tempItem.costsGold>=0) {
			hero.gold-=tempItem.costsGold;
		} else {
			Cookie c = hero.createCookie();
			model.addAttribute("resource",resourceCookie);
			c.setPath("/");
			c.setMaxAge(60*60*24*2);
			response.addCookie(c);
			model.addAttribute("message",hero.createDisplayText());
			model.addAttribute("notEnoughGold","Not enough Gold for");
			return "shopped";
		}

		if (tempItem.healthLimit != 0) {
			hero.maxHp += tempItem.healthLimit;
		}
		if (tempItem.currentHealth!=0) {
			if(hero.hp + tempItem.currentHealth < hero.maxHp) {
				hero.hp += tempItem.currentHealth;
			} else {
				hero.hp = hero.maxHp;
			}
		}
		
		if(tempItem.maxMana !=0)
		{
			hero.maxMana+=tempItem.maxMana;
		}
		if (tempItem.mana != 0) {
			if (hero.mana + tempItem.mana < hero.maxMana) {
				hero.mana += tempItem.mana;
			} else {
				hero.mana = hero.maxMana;
			}
		}
		if (tempItem.attackMin != 0) {
			hero.attackMin += tempItem.attackMin;
		}
		if (tempItem.attackMax != 0) {
			hero.attackMax += tempItem.attackMax;
		}
		if (tempItem.armor != 0) {
			hero.armor += tempItem.armor;
		}
		if (tempItem.magicResist != 0) {
			hero.magicResist += tempItem.magicResist;
		}
		if (tempItem.critChance != 0) {
			hero.critChance+=tempItem.critChance;
		}
		
		Cookie c = hero.createCookie();
		model.addAttribute("resource", resourceCookie);
		c.setPath("/");
		c.setMaxAge(60 * 60 * 24 * 2);
		response.addCookie(c);
		model.addAttribute("message", hero.createDisplayText());
		return "shopped";
	}
	
	@RequestMapping(value="/item2",method=RequestMethod.GET)
	String theItem2(ModelMap model,HttpServletResponse response,@CookieValue("resource")String resourceCookie)
	{
		if(hero.gold-tempItem2.costsGold>=0) {
			hero.gold-=tempItem2.costsGold;
		} else {
			Cookie c = hero.createCookie();
			model.addAttribute("resource",resourceCookie);
			c.setPath("/");
			c.setMaxAge(60*60*24*2);
			response.addCookie(c);
			model.addAttribute("message",hero.createDisplayText());
			model.addAttribute("notEnoughGold","Not enough Gold for");
			return "shopped";
		}
		if (tempItem2.healthLimit != 0) {
			hero.maxHp += tempItem2.healthLimit;
		}
		if (tempItem2.currentHealth!=0) {
			if(hero.hp + tempItem2.currentHealth < hero.maxHp) {
				hero.hp += tempItem2.currentHealth;
			} else {
				hero.hp = hero.maxHp;
			}
		}
		if(tempItem2.maxMana !=0)
		{
			hero.maxMana+=tempItem2.maxMana;
		}
		if (tempItem2.mana != 0) {
			if (hero.mana + tempItem2.mana < hero.maxMana) {
				hero.mana += tempItem2.mana;
			} else {
				hero.mana = hero.maxMana;
			}
		}
		if (tempItem2.attackMin != 0) {
			hero.attackMin += tempItem2.attackMin;
		}
		if (tempItem2.attackMax != 0) {
			hero.attackMax += tempItem2.attackMax;
		}
		if (tempItem2.armor != 0) {
			hero.armor += tempItem2.armor;
		}
		if (tempItem2.magicResist != 0) {
			hero.magicResist += tempItem2.magicResist;
		}
		if (tempItem2.critChance != 0) {
			hero.critChance+=tempItem2.critChance;
		}
		
		Cookie c = hero.createCookie();
		model.addAttribute("resource", resourceCookie);
		c.setPath("/");
		c.setMaxAge(60 * 60 * 24 * 2);
		response.addCookie(c);
		model.addAttribute("message", hero.createDisplayText());
		return "shopped";
	}
	
	@RequestMapping(value="/item3",method=RequestMethod.GET)
	String theItem3(ModelMap model,HttpServletResponse response,@CookieValue("resource")String resourceCookie)
	{
		if(hero.gold-tempItem3.costsGold>=0) {
			hero.gold-=tempItem3.costsGold;
		} else {
			Cookie c = hero.createCookie();
			model.addAttribute("resource",resourceCookie);
			c.setPath("/");
			c.setMaxAge(60*60*24*2);
			response.addCookie(c);
			model.addAttribute("message",hero.createDisplayText());
			model.addAttribute("notEnoughGold","Not enough Gold for");
			return "shopped";
		}
		
		if (tempItem3.healthLimit != 0) {
			hero.maxHp += tempItem3.healthLimit;
		}
		if (tempItem3.currentHealth!=0) {
			if(hero.hp + tempItem3.currentHealth < hero.maxHp) {
				hero.hp += tempItem3.currentHealth;
			} else {
				hero.hp = hero.maxHp;
			}
		}
		
		if(tempItem3.maxMana !=0)
		{
			hero.maxMana+=tempItem3.maxMana;
		}
		if (tempItem3.mana != 0) {
			if (hero.mana + tempItem3.mana < hero.maxMana) {
				hero.mana += tempItem3.mana;
			} else {
				hero.mana = hero.maxMana;
			}
		}
		if (tempItem3.attackMin != 0) {
			hero.attackMin += tempItem3.attackMin;
		}
		if (tempItem3.attackMax != 0) {
			hero.attackMax += tempItem3.attackMax;
		}
		if (tempItem3.armor != 0) {
			hero.armor += tempItem3.armor;
		}
		if (tempItem3.magicResist != 0) {
			hero.magicResist += tempItem3.magicResist;
		}
		if (tempItem3.critChance != 0) {
			hero.critChance+=tempItem3.critChance;
		}
		
		Cookie c = hero.createCookie();
		model.addAttribute("resource", resourceCookie);
		c.setPath("/");
		c.setMaxAge(60 * 60 * 24 * 2);
		response.addCookie(c);
		model.addAttribute("message", hero.createDisplayText());
		return "shopped";
	}
	
	@RequestMapping(value="/item4",method=RequestMethod.GET)
	String theItem4(ModelMap model,HttpServletResponse response,@CookieValue("resource")String resourceCookie)
	{
		if(hero.gold-tempItem4.costsGold>=0) {
			hero.gold-=tempItem4.costsGold;
		} else {
			Cookie c = hero.createCookie();
			model.addAttribute("resource",resourceCookie);
			c.setPath("/");
			c.setMaxAge(60*60*24*2);
			response.addCookie(c);
			model.addAttribute("message",hero.createDisplayText());
			model.addAttribute("notEnoughGold","Not enough Gold for");
			return "shopped";
		}
		
		if (tempItem4.healthLimit != 0) {
			hero.maxHp += tempItem4.healthLimit;
		}
		if (tempItem4.currentHealth!=0) {
			if(hero.hp + tempItem4.currentHealth < hero.maxHp) {
				hero.hp += tempItem4.currentHealth;
			} else {
				hero.hp = hero.maxHp;
			}
		}
		
		if(tempItem4.maxMana !=0)
		{
			hero.maxMana+=tempItem4.maxMana;
		}
		if (tempItem4.mana != 0) {
			if (hero.mana + tempItem4.mana < hero.maxMana) {
				hero.mana += tempItem4.mana;
			} else {
				hero.mana = hero.maxMana;
			}
		}
		if (tempItem4.attackMin != 0) {
			hero.attackMin += tempItem4.attackMin;
		}
		if (tempItem4.attackMax != 0) {
			hero.attackMax += tempItem4.attackMax;
		}
		if (tempItem4.armor != 0) {
			hero.armor += tempItem4.armor;
		}
		if (tempItem4.magicResist != 0) {
			hero.magicResist += tempItem4.magicResist;
		}
		if (tempItem4.critChance != 0) {
			hero.critChance+=tempItem4.critChance;
		}
		
		Cookie c = hero.createCookie();
		model.addAttribute("resource", resourceCookie);
		c.setPath("/");
		c.setMaxAge(60 * 60 * 24 * 2);
		response.addCookie(c);
		model.addAttribute("message", hero.createDisplayText());
		return "shopped";
	}
	
	@RequestMapping(value="/item5",method=RequestMethod.GET)
	String theItem5(ModelMap model,HttpServletResponse response,@CookieValue("resource")String resourceCookie)
	{
		if(hero.gold-tempItem5.costsGold>=0) {
			hero.gold-=tempItem5.costsGold;
		} else {
			Cookie c = hero.createCookie();
			model.addAttribute("resource",resourceCookie);
			c.setPath("/");
			c.setMaxAge(60*60*24*2);
			response.addCookie(c);
			model.addAttribute("message",hero.createDisplayText());
			model.addAttribute("notEnoughGold","Not enough Gold for");
			return "shopped";
		}
		if (tempItem5.healthLimit != 0) {
			hero.maxHp += tempItem5.healthLimit;
		}
		if (tempItem5.currentHealth!=0) {
			if(hero.hp + tempItem5.currentHealth < hero.maxHp) {
				hero.hp += tempItem5.currentHealth;
			} else {
				hero.hp = hero.maxHp;
			}
		}
		if(tempItem5.maxMana !=0)
		{
			hero.maxMana+=tempItem5.maxMana;
		}
		if (tempItem5.mana != 0) {
			if (hero.mana + tempItem5.mana < hero.maxMana) {
				hero.mana += tempItem5.mana;
			} else {
				hero.mana = hero.maxMana;
			}
		}
		if (tempItem5.attackMin != 0) {
			hero.attackMin += tempItem5.attackMin;
		}
		if (tempItem5.attackMax != 0) {
			hero.attackMax += tempItem5.attackMax;
		}
		if (tempItem5.armor != 0) {
			hero.armor += tempItem5.armor;
		}
		if (tempItem5.magicResist != 0) {
			hero.magicResist += tempItem5.magicResist;
		}
		if (tempItem5.critChance != 0) {
			hero.critChance+=tempItem5.critChance;
		}
		
		Cookie c = hero.createCookie();
		model.addAttribute("resource", resourceCookie);
		c.setPath("/");
		c.setMaxAge(60 * 60 * 24 * 2);
		response.addCookie(c);
		model.addAttribute("message", hero.createDisplayText());
		return "shopped";
	}
	
	
}