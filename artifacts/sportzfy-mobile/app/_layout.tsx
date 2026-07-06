import { Stack } from 'expo-router';
import { StatusBar } from 'expo-status-bar';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { View, StyleSheet } from 'react-native';
import { PlayerProvider } from '../context/PlayerContext';
import FloatingPlayer from '../components/FloatingPlayer';
import { Colors } from '../constants/colors';

export default function RootLayout() {
  return (
    <GestureHandlerRootView style={styles.root}>
      <PlayerProvider>
        <StatusBar style="light" backgroundColor={Colors.bg} />

        <Stack
          screenOptions={{
            headerShown: false,
            contentStyle: { backgroundColor: Colors.bg },
            animation: 'fade',
          }}
        >
          <Stack.Screen name="index" />
          <Stack.Screen
            name="player"
            options={{
              animation: 'slide_from_bottom',
              contentStyle: { backgroundColor: '#000' },
            }}
          />
          <Stack.Screen name="+not-found" />
        </Stack>

        {/* Floating mini-player — renders on top of all screens */}
        <FloatingPlayer />
      </PlayerProvider>
    </GestureHandlerRootView>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: Colors.bg,
  },
});
