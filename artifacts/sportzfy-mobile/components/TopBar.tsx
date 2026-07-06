import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  StatusBar,
  Platform,
} from 'react-native';
import { Colors } from '../constants/colors';

interface Props {
  onMenuPress: () => void;
  onSearch: (query: string) => void;
}

export default function TopBar({ onMenuPress, onSearch }: Props) {
  const [searchText, setSearchText] = useState('');
  const [focused, setFocused] = useState(false);

  const handleChange = (text: string) => {
    setSearchText(text);
    onSearch(text);
  };

  const clearSearch = () => {
    setSearchText('');
    onSearch('');
  };

  return (
    <View style={styles.container}>
      {/* Left: Logo + Title */}
      <View style={styles.left}>
        <View style={styles.logoMark}>
          <Text style={styles.logoIcon}>⚡</Text>
        </View>
        {!focused && <Text style={styles.title}>SPORTZFY</Text>}
      </View>

      {/* Center: Search */}
      <View style={[styles.searchBox, focused && styles.searchBoxFocused]}>
        <Text style={styles.searchIcon}>🔍</Text>
        <TextInput
          style={styles.searchInput}
          placeholder="Search matches…"
          placeholderTextColor={Colors.textDim}
          value={searchText}
          onChangeText={handleChange}
          onFocus={() => setFocused(true)}
          onBlur={() => setFocused(false)}
          returnKeyType="search"
        />
        {searchText.length > 0 && (
          <TouchableOpacity onPress={clearSearch} hitSlop={{ top: 8, bottom: 8, left: 8, right: 8 }}>
            <Text style={styles.clearIcon}>✕</Text>
          </TouchableOpacity>
        )}
      </View>

      {/* Right: Menu */}
      <TouchableOpacity style={styles.menuBtn} onPress={onMenuPress} activeOpacity={0.7}>
        <View style={styles.menuLines}>
          <View style={styles.menuLine} />
          <View style={[styles.menuLine, { width: 16 }]} />
          <View style={[styles.menuLine, { width: 12 }]} />
        </View>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 14,
    paddingTop: Platform.OS === 'ios' ? 50 : (StatusBar.currentHeight || 0) + 10,
    paddingBottom: 12,
    backgroundColor: Colors.bg,
    borderBottomWidth: 1,
    borderBottomColor: Colors.border,
    gap: 10,
  },
  left: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    minWidth: 40,
  },
  logoMark: {
    width: 34,
    height: 34,
    borderRadius: 10,
    backgroundColor: Colors.card,
    borderWidth: 1.5,
    borderColor: Colors.accent,
    alignItems: 'center',
    justifyContent: 'center',
  },
  logoIcon: {
    fontSize: 16,
  },
  title: {
    fontSize: 15,
    fontWeight: '900',
    color: Colors.accent,
    letterSpacing: 2,
  },
  searchBox: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: Colors.card,
    borderRadius: 12,
    paddingHorizontal: 12,
    height: 38,
    borderWidth: 1,
    borderColor: Colors.border,
    gap: 8,
  },
  searchBoxFocused: {
    borderColor: Colors.accent,
    backgroundColor: Colors.cardHover,
  },
  searchIcon: {
    fontSize: 13,
  },
  searchInput: {
    flex: 1,
    color: Colors.text,
    fontSize: 13,
    padding: 0,
  },
  clearIcon: {
    fontSize: 12,
    color: Colors.textMuted,
  },
  menuBtn: {
    width: 38,
    height: 38,
    borderRadius: 10,
    backgroundColor: Colors.card,
    borderWidth: 1,
    borderColor: Colors.border,
    alignItems: 'center',
    justifyContent: 'center',
  },
  menuLines: {
    gap: 4,
    alignItems: 'flex-end',
  },
  menuLine: {
    width: 20,
    height: 2,
    borderRadius: 1,
    backgroundColor: Colors.accent,
  },
});
