package com.aarestu.controller;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.Random;
@Controller
public class FightController {
	String theBadCookie="";
	String resource="";
	Hero hero;
	Enemy enemy;
	int rangerSightBonusDamageMin=0;
	int rangerSightBonusDamageMax=0;
	int bloodlustBonusDamageMin=0;
	int bloodlustBonusDamageMax=0;
	int berserkPassiveDamageIncrease=0;
	int berserkCritical=0;
	final static Logger logger=Logger.getLogger(FightController.class);
	@RequestMapping(value="/fight",method = RequestMethod.GET)
	public String fight(ModelMap model, @CookieValue("hero") String fooCookie, @CookieValue(value="enemy",defaultValue="-1001") String badCookie,@CookieValue(value="resource",defaultValue="-1001") String resourceCookie,
			HttpServletResponse response) {
	
		resource=resourceCookie;
		logger.debug("the bad Cookie is: "+badCookie);
		hero = Hero.fromCookie(fooCookie);
		if (hero == null) {
			return "defeat";
		}
		model.addAttribute("resource",resourceCookie);
		logger.debug("enemy123:  "+badCookie);
		enemy = Enemy.fromCookie(badCookie);

		String fightOutcome=fight(enemy.health,enemy.attackType,enemy.damageMin,enemy.damageMax,enemy.armor,enemy.dropsGold,enemy.critChance,0,0,response,model,hero);
		if(fightOutcome.equals("nobodyDied"))
		{
			return "fight";
		}
		return fightOutcome;
	}

	
	int attack(int min, int max) {
	   int range = (max - min) + 1;     
	   return (int)(Math.random() * range) + min;
	}
	boolean critical(int critChance) {
		return Math.random() * 100 < critChance;
	}

	
	String fight(int enemyHealth,int attackType, int enemyAttackMin, int enemyAttackMax, 
			int enemyArmor, int dropsGold, int enemyCritChance,int rangerSightBonusDamageMin,int rangerSightBonusDamageMax, HttpServletResponse response, ModelMap model,Hero hero) {
		int defense;
		resource=enemy.name;
		if(hero.heroClass.equals("Mage"))
		{
			hero.attackMin+=hero.magicResist/2;
			hero.attackMax+=hero.magicResist/2;
			model.addAttribute("spell","Fireball");
		}else if(hero.heroClass.equals("Warrior"))
		{
			hero.attackMin+=hero.armor/2;
			hero.attackMax+=hero.armor/2;
			model.addAttribute("spell","Endurance");
		}
		if(hero.heroClass.equals("Ranger"))
		{
			model.addAttribute("yourPetAttacks","Your pet attacks");
			model.addAttribute("dealing"," dealing ");
			model.addAttribute("spell","Ranger Sight");
			int petDamageMin;
			int petDamageMax;
			if(rangerSightBonusDamageMax!=0 && rangerSightBonusDamageMin!=0)
			{
				 petDamageMax=(int)((hero.attackMax+rangerSightBonusDamageMax)*0.15);
				 petDamageMin=(int)((hero.attackMin+rangerSightBonusDamageMin)*0.15);
			}else {
				 petDamageMin=(int)(hero.attackMin*0.15);
				 petDamageMax=(int)(hero.attackMax*0.15);
			}
			
			int tempEnemyH=enemyHealth;
			boolean petCrit=critical(hero.critChance);
			if (petCrit) {
				model.addAttribute("petCritically"," CRITICALLY");
				enemyHealth-=attack(petDamageMin,petDamageMax)*1.8;
				tempEnemyH-=enemyHealth;
				model.addAttribute("petDamage",String.valueOf(tempEnemyH)+" Damage");
			}else {
				
				enemyHealth-=attack(petDamageMin,petDamageMax);
				tempEnemyH-=enemyHealth;
				model.addAttribute("petDamage",String.valueOf(tempEnemyH)+" Damage");
			}
			
		}
		if (hero.heroClass.equals("Berserk")) {
			model.addAttribute("spell", "Bloodlust");
			
				berserkPassiveDamageIncrease = (hero.maxHp - hero.hp) / 25;
			
		}
		if(attackType==1) {
			defense=hero.armor;
		} else {
			defense=hero.magicResist;
		}
		int tempEnemyHealth=enemyHealth;
		boolean crit=critical(hero.critChance+berserkCritical);
		double multiply=1;
		if(crit==true) {
			model.addAttribute("critically","CRITICALLY ");
			multiply=1.8;
		} else {
			model.addAttribute("critically","");
		}
		logger.debug("berserk bonus damage is :"+berserkPassiveDamageIncrease);
		logger.debug("BLoodlust bonus damage min and max are: "+bloodlustBonusDamageMin+"-"+bloodlustBonusDamageMax);
		logger.debug("Damage Min is: "+hero.attackMin);
		logger.debug("Damage max is:" +hero.attackMax);
		
		if ((attack(hero.attackMin+rangerSightBonusDamageMin+berserkPassiveDamageIncrease+bloodlustBonusDamageMin,hero.attackMax+rangerSightBonusDamageMax+berserkPassiveDamageIncrease+bloodlustBonusDamageMax)*multiply - enemyArmor)<0) {
			enemyHealth=enemyHealth-1;
		} else {
			enemyHealth =(int) (enemyHealth - (attack(hero.attackMin+rangerSightBonusDamageMin+berserkPassiveDamageIncrease+bloodlustBonusDamageMin, hero.attackMax+rangerSightBonusDamageMax+berserkPassiveDamageIncrease+bloodlustBonusDamageMax) * multiply - enemyArmor));
		}
		rangerSightBonusDamageMin=0;
		rangerSightBonusDamageMax=0;
		if (enemyHealth <= 0) {
			hero.gold += dropsGold;
			if(hero.heroClass.equals("Mage"))
			{
				hero.attackMin-=hero.magicResist/2;
				hero.attackMax-=hero.magicResist/2;
			}
			else if(hero.heroClass.equals("Warrior"))
			{
				hero.attackMin-=hero.armor/2;
				hero.attackMax-=hero.armor/2;
			}
			Cookie c = hero.createCookie();

			c.setPath("/");
			c.setMaxAge(60 * 60 * 24 * 2);
			response.addCookie(c);
			model.addAttribute("gold",String.valueOf(dropsGold));
			Cookie bossState=new Cookie("bossState","dead");
			bossState.setPath("/");
			bossState.setMaxAge(60*60*24*2);
			response.addCookie(bossState);
			berserkPassiveDamageIncrease=0;
			bloodlustBonusDamageMin=0;
			bloodlustBonusDamageMax=0;
			berserkCritical=0;
			return "fightvictory";
		}
		int tempHealth=hero.hp;
		crit=critical(enemyCritChance);
		multiply=1;
		if(crit==true) {
			model.addAttribute("enemyCritically","CRITICALLY ");
			multiply=2;
		} else {
			model.addAttribute("enemyCritically","");
		}
		if ((attack(enemyAttackMin,enemyAttackMax) - hero.armor) < 0) {
			hero.hp = hero.hp - 1;
		} else {
			hero.hp =(int) (hero.hp - (attack(enemyAttackMin,enemyAttackMax)*multiply - defense));
		}
		if (hero.hp <= 0) {
			Cookie c = hero.createCookie();

			c.setPath("/");
			c.setMaxAge(60 * 60 * 24 * 2);
			response.addCookie(c);
			ArrayList<String> images=new ArrayList<String>();
			
			images.add("defeat");
			images.add("defeat2");
			images.add("defeat3");
			images.add("defeat4");
			images.add("defeat5");
			images.add("defeat6");
			
			int theIndex=attack(0,images.size()-1);
			model.addAttribute("defeatScreen",images.get(theIndex));
			Cookie enem=new Cookie("enemy","greshka");
			enem.setPath("/");
			enem.setMaxAge(60*60*24*2);
			response.addCookie(enem);
			berserkCritical=0;
			berserkPassiveDamageIncrease=0;
			bloodlustBonusDamageMin=0;
			bloodlustBonusDamageMax=0;
			return "defeat";

		}
		if(hero.heroClass.equals("Mage"))
		{
			hero.attackMin-=hero.magicResist/2;
			hero.attackMax-=hero.magicResist/2;
		}
		else if(hero.heroClass.equals("Warrior"))
		{
			hero.attackMin-=hero.armor/2;
			hero.attackMax-=hero.armor/2;
		}
		berserkPassiveDamageIncrease=0;
		bloodlustBonusDamageMin=0;
		bloodlustBonusDamageMax=0;
		berserkCritical=0;
		Cookie c = hero.createCookie();
		enemy.health=enemyHealth;
		theBadCookie=enemy.toCookie();
		Cookie e = new Cookie("enemy", theBadCookie);
		e.setMaxAge(60 * 60 * 24 * 2);
		e.setPath("/");
		c.setPath("/");
		c.setMaxAge(60 * 60 * 24 * 2);
		response.addCookie(c);
		response.addCookie(e);
		int damageDealt = tempEnemyHealth-enemyHealth;
		int enemyDamage=tempHealth-hero.hp;
		model.addAttribute("message2", hero.createDisplayText());
		model.addAttribute("damageDealt", String.valueOf(damageDealt));
		model.addAttribute("enemy", String.valueOf(enemy.health));
		model.addAttribute("enemyName",resource);
		model.addAttribute("enemyDamage",String.valueOf(enemyDamage));
		return "nobodyDied";
	}
	
	@RequestMapping(value="/fightWithSpell",method=RequestMethod.GET)
	 String fightWithSpell(ModelMap model, HttpServletResponse response,@CookieValue("enemy")String enemyCookie,@CookieValue("resource")String resourceCookie,@CookieValue("hero")String heroCookie)
	{
		Hero hero=Hero.fromCookie(heroCookie);
		model.addAttribute("resource",resourceCookie);
		if (hero.heroClass.equals("Mage")) {
			if (hero.mana - 20 < 0) {
				model.addAttribute("message", hero.createDisplayText());
				return "noMana";

			}
			hero.mana -= 20;
			String critically = "";
			enemy = Enemy.fromCookie(enemyCookie);
			int currentEnemyHealth = enemy.health;
			if (critical(hero.critChance)) {
				enemy.health -= (hero.maxMana * 0.30) * 1.8;
				critically = " CRITICALLY";

			} else {
				enemy.health -= hero.maxMana * 0.30;
			}
			currentEnemyHealth = currentEnemyHealth - enemy.health;
			model.addAttribute("spellDamage", "You cast Fireball" + critically + " Damaging the enemy for "
					+ String.valueOf(currentEnemyHealth) + " Damage");
			Cookie leHeroCookie = hero.createCookie();
			leHeroCookie.setPath("/");
			leHeroCookie.setMaxAge(60 * 60 * 24 * 2);
			response.addCookie(leHeroCookie);
			String fightOutcome = fight(enemy.health, enemy.attackType, enemy.damageMin, enemy.damageMax, enemy.armor,
					enemy.dropsGold, enemy.critChance,0,0, response, model, hero);
			if (fightOutcome.equals("nobodyDied")) {
				return "fight";
			}
			return fightOutcome;
		}
		if (hero.heroClass.equals("Warrior")) {
			if (hero.mana - 20 < 0) {
				model.addAttribute("message", hero.createDisplayText());
				return "noMana";

			}
			hero.mana -= 20;
			enemy = Enemy.fromCookie(enemyCookie);
			int currentEnemyHealth = enemy.health;
			enemy.health -= (hero.maxHp - hero.hp) * 0.10;
			currentEnemyHealth = currentEnemyHealth - enemy.health;
			hero.hp += currentEnemyHealth;
			model.addAttribute("spellDamage", "You cast Endurance Damaging the enemy for "
					+ String.valueOf(currentEnemyHealth) + " and healing yourself for that amount.");
			Cookie leHeroCookie = hero.createCookie();
			leHeroCookie.setPath("/");
			leHeroCookie.setMaxAge(60 * 60 * 24 * 2);
			response.addCookie(leHeroCookie);
			String fightOutcome = fight(enemy.health, enemy.attackType, enemy.damageMin, enemy.damageMax, enemy.armor,
					enemy.dropsGold, enemy.critChance,0,0, response, model, hero);
			if (fightOutcome.equals("nobodyDied")) {
				return "fight";
			}
			return fightOutcome;
		}
		if (hero.heroClass.equals("Ranger")) {
			if (hero.mana - 20 < 0) {
				model.addAttribute("message", hero.createDisplayText());
				return "noMana";

			}
			hero.mana -= 20;
			enemy = Enemy.fromCookie(enemyCookie);
			//int currentEnemyHealth = enemy.health;
			//enemy.health -= (hero.maxHp - hero.hp) * 0.10;
			//currentEnemyHealth = currentEnemyHealth - enemy.health;
			//hero.hp += currentEnemyHealth;
			rangerSightBonusDamageMin=(int)(hero.attackMin*0.60);
			rangerSightBonusDamageMax=(int)(hero.attackMax*0.60);
			model.addAttribute("spellDamage", "You cast Ranger Sight increasing your Damage to "+String.valueOf(hero.attackMin+rangerSightBonusDamageMin)+"-"
					+ String.valueOf(hero.attackMax+rangerSightBonusDamageMax));
			Cookie leHeroCookie = hero.createCookie();
			leHeroCookie.setPath("/");
			leHeroCookie.setMaxAge(60 * 60 * 24 * 2);
			response.addCookie(leHeroCookie);
			String fightOutcome = fight(enemy.health, enemy.attackType, enemy.damageMin, enemy.damageMax, enemy.armor,
					enemy.dropsGold, enemy.critChance,rangerSightBonusDamageMin,rangerSightBonusDamageMax, response, model, hero);
			if (fightOutcome.equals("nobodyDied")) {
				return "fight";
			}
			return fightOutcome;
		}
		if(hero.heroClass.equals("Berserk")){
			if (hero.mana - 20 < 0) {
				model.addAttribute("message", hero.createDisplayText());
				return "noMana";

			}
			hero.mana -= 20;
			enemy = Enemy.fromCookie(enemyCookie);
			double bloodlustPercentIncrease=1.5*hero.critChance;
			int berserkPassiveDamage=(hero.maxHp-hero.hp)/25;
			bloodlustBonusDamageMin=(int)((hero.attackMin+berserkPassiveDamage)*(bloodlustPercentIncrease)*0.01);
			bloodlustBonusDamageMax=(int)((hero.attackMax+berserkPassiveDamage)*(bloodlustPercentIncrease)*0.01);
			model.addAttribute("spellDamage", "You cast Bloodlust  increasing your Damage by "+String.valueOf(bloodlustPercentIncrease)+"%.Your damage is now "+String.valueOf(hero.attackMin+berserkPassiveDamage+bloodlustBonusDamageMin)+"-"
					+ String.valueOf(hero.attackMax+berserkPassiveDamage+bloodlustBonusDamageMax)+" and your next attack is a critical");
			Cookie leHeroCookie = hero.createCookie();
			leHeroCookie.setPath("/");
			leHeroCookie.setMaxAge(60 * 60 * 24 * 2);
			response.addCookie(leHeroCookie);
			berserkCritical=100;
			String fightOutcome = fight(enemy.health, enemy.attackType, enemy.damageMin, enemy.damageMax, enemy.armor,
					enemy.dropsGold, enemy.critChance,rangerSightBonusDamageMin,rangerSightBonusDamageMax, response, model, hero);
			if (fightOutcome.equals("nobodyDied")) {
				return "fight";
			}
			return fightOutcome;
		}
		else {
			return "fight";
		}
		

	}
}
