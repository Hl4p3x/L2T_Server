/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ai.individual.Summons;

import ai.group_template.L2AttackableAIScript;
import l2server.gameserver.GeoData;
import l2server.gameserver.ThreadPoolManager;
import l2server.gameserver.datatables.SkillTable;
import l2server.gameserver.model.L2Party;
import l2server.gameserver.model.actor.L2Summon;
import l2server.gameserver.model.actor.instance.L2PcInstance;

import java.util.concurrent.ScheduledFuture;

/**
 * @author LasTravel
 * @author Pere
 *         <p>
 *         Summon Tree of Life (skill id: 11774) AI
 */

public class TreeOfLife extends L2AttackableAIScript
{
	private static final int[] treeOfLifeIds =
			{14933, 14943, 15010, 15011, 15012, 15013, 15014, 15015, 15016, 15017, 15018, 15019, 15020, 15021};
	private static final int blessingOfLifeId = 11806;

	public TreeOfLife(int id, String name, String descr)
	{
		super(id, name, descr);

		for (int i : treeOfLifeIds)
		{
			addSpawnId(i);
		}
	}

	@Override
	public final String onSpawn(L2Summon npc)
	{
		TreeOfLifeAI ai = new TreeOfLifeAI(npc);

		ai.setSchedule(ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(ai, 5000, 10000));

		return null;
	}

	class TreeOfLifeAI implements Runnable
	{
		private L2Summon treeOfLife;
		private L2PcInstance owner;
		private ScheduledFuture<?> schedule = null;

		protected TreeOfLifeAI(L2Summon npc)
		{
			treeOfLife = npc;
			owner = npc.getOwner();
		}

		public void setSchedule(ScheduledFuture<?> schedule)
		{
			this.schedule = schedule;
		}

		@Override
		public void run()
		{
			if (treeOfLife == null || treeOfLife.isDead() || !owner.getSummons().contains(treeOfLife))
			{
				if (schedule != null)
				{
					schedule.cancel(true);
					return;
				}
			}

			L2Party party = treeOfLife.getOwner().getParty();

			if (party != null)
			{
				for (L2PcInstance player : party.getPartyMembers())
				{
					if (player == null || !GeoData.getInstance().canSeeTarget(treeOfLife, player))
					{
						continue;
					}

					SkillTable.getInstance().getInfo(blessingOfLifeId, treeOfLife.getSkillLevelHash(blessingOfLifeId))
							.getEffects(treeOfLife, player);
				}
			}
			else
			{
				if (GeoData.getInstance().canSeeTarget(treeOfLife, owner))
				{
					SkillTable.getInstance().getInfo(blessingOfLifeId, treeOfLife.getSkillLevelHash(blessingOfLifeId))
							.getEffects(treeOfLife, owner);
				}
			}
		}
	}

	public static void main(String[] args)
	{
		new TreeOfLife(-1, "TreeOfLife", "ai/individual");
	}
}
