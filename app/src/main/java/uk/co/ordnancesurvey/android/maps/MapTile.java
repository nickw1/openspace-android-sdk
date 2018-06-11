/**
 * OpenSpace Android SDK Licence Terms
 *
 * The OpenSpace Android SDK is protected by © Crown copyright – Ordnance Survey 2013.[https://github.com/OrdnanceSurvey]
 *
 * All rights reserved (subject to the BSD licence terms as follows):.
 *
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * Neither the name of Ordnance Survey nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 *
 * Amendments Nick Whitelegg (NW) 110618
 * - made public to allow use of custom tile servers
 * - added appropriate getter methods
 */
package uk.co.ordnancesurvey.android.maps;

public final class MapTile {
	MapLayer layer;
	int x;
	int y;
	MapTile() {
	}
	
	MapTile(MapTile copy) {
		x = copy.x;
		y = copy.y;
		layer = copy.layer;
	}
	void set(int xx,int yy,MapLayer l) {
		x = xx;
		y = yy;
		layer = l;
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			// This is expected of anything overriding Object.equals()
			return false;
		}

		try {
			MapTile other = (MapTile)o;
			assert this.getClass() == o.getClass();
			return layer == other.layer && x == other.x && y == other.y;
		} catch (ClassCastException e) {
			assert this.getClass() != o.getClass();
			assert !super.equals(o) : "Object.equals() compares object identity so it should never be true here.";
			return false;
		}
	}
	@Override
	public int hashCode() {
		return layer.hashCode() ^ (x*8191+y);
	}

	// NW 110618 added
	public int getX() {
		return x;
	}

	// NW 110618 added
	public int getY() {
		return y;
	}

	// NW 110618 added
	public MapLayer getMapLayer() {
		return layer;
	}
}
